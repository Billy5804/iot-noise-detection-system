export default [
  {
    key: "NOISE",
    label: "Noise level detector",
    unicodeIcon: "\uf2a2",
    icon: "ear-listen",
  },
].reduce((result, { key, label, unicodeIcon, icon }, index) => {
  result[key] = {
    getUnicodeIcon: () => unicodeIcon,
    getIcon: () => icon,
    getLabel: () => label,
    toString: () => key,
    ordinal: () => index,
  };
  return result;
}, {});
