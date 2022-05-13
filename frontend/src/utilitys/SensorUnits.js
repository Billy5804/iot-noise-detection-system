import Sensors from "./Sensors";

export default [
  {
    key: "DECIBEL",
    symbol: "db",
    sensorType: Sensors.SOUND,
    warningThreshold: 60,
    dangerThreshold: 70,
  },
  {
    key: "BEL",
    symbol: "B",
    sensorType: Sensors.SOUND,
    warningValue: 6,
    dangerValue: 7,
  },
].reduce((result, { key, symbol, sensorType }, index) => {
  result[key] = {
    getSymbol: () => symbol,
    getSensorType: () => sensorType,
    toString: () => key,
    ordinal: () => index,
    getWarningThreshold: () => 60,
    getDangerThreshold: () => 70,
  };
  return result;
}, {});
