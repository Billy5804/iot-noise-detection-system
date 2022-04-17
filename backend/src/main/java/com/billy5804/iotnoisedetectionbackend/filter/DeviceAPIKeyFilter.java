package com.billy5804.iotnoisedetectionbackend.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.billy5804.iotnoisedetectionbackend.model.DeviceAuthenticationToken;

public class DeviceAPIKeyFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final BodyCachingRequestWrapper wrapedRequest = new BodyCachingRequestWrapper(request);
		final String credentials = request.getHeader("Authorization");
		final byte[] principal = wrapedRequest.getInputStream().readAllBytes();
		try {
			SecurityContextHolder.getContext().setAuthentication(new DeviceAuthenticationToken(principal, credentials));
			filterChain.doFilter(wrapedRequest, response);
		} catch (SecurityException e) {
			response.reset();
			response.addHeader("WWW-authenticate", e.getMessage());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	private class BodyCachingRequestWrapper extends HttpServletRequestWrapper {

		private byte[] body;

		public BodyCachingRequestWrapper(HttpServletRequest request) throws IOException {
			super(request);
			this.body = request.getInputStream().readAllBytes();
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {
			return new CachedBodyInputStreamWrapper(this.body);
		}

		private class CachedBodyInputStreamWrapper extends ServletInputStream {

			private final InputStream inputStream;

			public CachedBodyInputStreamWrapper(byte[] body) {
				this.inputStream = new ByteArrayInputStream(body);
			}

			@Override
			public boolean isFinished() {
				try {
					return inputStream.available() == 0;
				} catch (Exception e) {
					return false;
				}
			}

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setReadListener(ReadListener listener) {
			}

			@Override
			public int read() throws IOException {
				return this.inputStream.read();
			}
		}
	}
}
