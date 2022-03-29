export default ["UNAUTHORISED", "OWNER", "EDITOR", "VIEWER"].reduce(
  (result, value) => {
    result[value] = value;
    return result;
  },
  {}
);
