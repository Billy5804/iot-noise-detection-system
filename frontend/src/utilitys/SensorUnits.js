import Sensors from "./Sensors";

export default [
  { key: "DECIBEL", symbol: "db", sensorType: Sensors.SOUND },
  { key: "BEL", symbol: "B", sensorType: Sensors.SOUND },
].reduce((result, { key, symbol, sensorType }, index) => {
  result[key] = {
    getSymbol: () => symbol,
    getSensorType: () => sensorType,
    toString: () => key,
    ordinal: () => index,
  };
  return result;
}, {});
