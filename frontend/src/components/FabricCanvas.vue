<template>
  <canvas :ref="canvasSetup" class="w-100"></canvas>
</template>

<script>
import { fabric } from "fabric";

export default {
  props: {
    backgroundImage: String,
  },

  setup: function (props) {
    function canvasSetup(canvasRef) {
      if (!canvasRef) {
        return;
      }
      const canvasParent = canvasRef.parentElement;
      const canvas = new fabric.Canvas(canvasRef);
      fabric.Image.fromURL(props.backgroundImage, function (image) {
        canvas.setWidth(canvasParent.clientWidth);
        canvas.setHeight((canvas.width / image.width) * image.height);

        canvas.setBackgroundImage(image, canvas.renderAll.bind(canvas), {
          scaleX: canvas.getWidth() / image.width,
          scaleY: canvas.getHeight() / image.height,
        });
      });
      const rect = new fabric.Rect({
        fill: "red",
        width: 20,
        height: 20,
      });

      canvas.add(rect);
      console.log(canvas);

      // Start a resize observer on the parent of the canvas
      // new ResizeObserver(() => {

      //   const fabricWidth = canvas.getWidth();
      //   const fabricHeight = canvas.getHeight();
      //   const cssWidth = Math.min(canvasParent.clientWidth, fabricWidth);
      //   const ratio = fabricWidth / canvasParent.clientWidth;

      //   fabric.Object.prototype.set({
      //     cornerSize: ratio * 10,
      //     borderScaleFactor: ratio
      //   });

      //   fabric.Object.prototype.controls.mtr.offsetY = -25 * ratio;

      //   canvas.setDimensions(
      //     {
      //       width: cssWidth + 'px',
      //       height: cssWidth / (fabricWidth / fabricHeight) + 'px'
      //     },
      //     { cssOnly: true }
      //   )
      //     .requestRenderAll();

      // }).observe(canvasParent);
    }

    return { canvasSetup };
  },
};
</script>

<style>
.canvas-container {
  width: 100% !important;
  height: 75vh;
  max-height: 80vh;
}
</style>
