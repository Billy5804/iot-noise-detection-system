export default [{ key: "SOUND", icon: "microphone-lines" }].reduce(
  (result, { key, icon }, index) => {
    result[key] = {
      getIcon: () => icon,
      toString: () => key,
      ordinal: () => index,
    };
    return result;
  },
  {}
);
