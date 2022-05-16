package com.billy5804.iotnoisedetectionbackend.handler;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

// This allows for all 404 request to be handled by returning the frontend index.html
@ControllerAdvice
public class NotFoundHandler {
	@Value("${spring.resources.static-locations}" + "index.html")
	Resource defaultFile;

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<String> renderDefaultPage() {
		try {
			String body = StreamUtils.copyToString(defaultFile.getInputStream(), Charset.defaultCharset());
			return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(body);
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("There was an error completing the action.");
		}
	}
}
