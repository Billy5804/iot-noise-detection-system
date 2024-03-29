import { firebaseStorage } from "../firebase/database";
import { ref, uploadBytes } from "firebase/storage";
import { optimize as svgOptimise } from "svgo/dist/svgo.browser";

export async function svgOptimiseAndStore(file, filePath) {
  if (file.type !== "image/svg+xml") {
    throw "Invalid file. Try again";
  }

  let optimisedSVGString;

  ({ data: optimisedSVGString } = svgOptimise(await file.text(), {
    multipass: true,
    removeRasterImages: true,
  }));

  const fileRef = ref(firebaseStorage, filePath);
  const svgBlob = new Blob([optimisedSVGString], { type: "image/svg+xml" });

  await uploadBytes(fileRef, svgBlob);

  return URL.createObjectURL(svgBlob);
}
