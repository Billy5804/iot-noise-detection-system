<template>
  <div :class="loading ? 'position-relative h-100' : ''">
    <LoadingView v-if="loading" />
    <canvas ref="canvasRef"></canvas>
  </div>
</template>

<script>
import { fabric } from "fabric";
import { ref, onMounted, watch } from "vue";
import LoadingView from "@/views/LoadingView.vue";

export default {
  props: {
    floorPlanURL: { type: String, required: true },
    editable: { type: Boolean, default: false },
    locationDevices: Object,
    selectedDeviceId: String,
  },

  emits: ["update:selectedDeviceId", "deviceMoved"],

  components: { LoadingView },

  setup: function (props, { emit }) {
    const loading = ref(true);
    const canvasRef = ref(null);
    const deviceIconType = "deviceIcon";
    const deviceIcons = {};

    const rootComputedStyle = getComputedStyle(document.querySelector(":root"));
    const mdbPrimary = rootComputedStyle.getPropertyValue("--mdb-primary");
    const mdbWarning = rootComputedStyle.getPropertyValue("--mdb-warning");
    const mdbDanger = rootComputedStyle.getPropertyValue("--mdb-danger");

    function drawDevices(canvas, scale, width, height) {
      const fontSize = 25 * scale;

      Object.entries(deviceIcons).forEach(([deviceId, deviceIcon]) => {
        canvas.remove(deviceIcon);
        delete deviceIcons[deviceId];
      });
      canvas.renderAll();

      Object.entries(props.locationDevices || {}).forEach(
        ([deviceId, { type, positionX, positionY, sensors }]) => {
          const selected = deviceId === props.selectedDeviceId;
          const mainSensor = sensors[0];
          const warningOrDanger =
            mainSensor.latestValue >= mainSensor.unit.getDangerThreshold()
              ? mdbDanger
              : mainSensor.latestValue >= mainSensor.unit.getWarningThreshold()
              ? mdbWarning
              : "#000000";
          positionX =
            positionX === null
              ? fontSize / 2
              : positionX < 0
              ? 0
              : positionX > width
              ? width
              : positionX;
          positionY =
            positionY === null
              ? fontSize / 2
              : positionY < 0
              ? 0
              : positionY > height
              ? height
              : positionY;

          deviceIcons[deviceId] = new fabric.Text(type.getUnicodeIcon(), {
            deviceId,
            warningOrDanger,
            type: deviceIconType,
            left: positionX,
            top: positionY,
            originX: "center",
            originY: "center",
            fontSize: selected ? fontSize * 2 : fontSize,
            originalFontSize: fontSize,
            fontFamily: "FontAwesome",
            selectable: props.editable,
            editable: false,
            hoverCursor: "pointer",
            fill: selected ? mdbPrimary : warningOrDanger,
            stroke: "#ffffff",
            strokeWidth: 5,
            paintFirst: "stroke",
            _controlsVisibility: {
              mt: false,
              mb: false,
              ml: false,
              mr: false,
              bl: false,
              br: false,
              tl: false,
              tr: false,
              mtr: false,
            },
          });
          canvas.add(deviceIcons[deviceId]);
        }
      );
      canvas.renderAll();
    }

    function scaleCanvas(canvas, width, height, ratio) {
      canvas.setDimensions({ width: width * ratio, height: height * ratio });
      canvas.setZoom(ratio);
      canvas.renderAll();
    }

    onMounted(async () => {
      const floorPlan = await new Promise((resolve) =>
        fabric.Image.fromURL(props.floorPlanURL, resolve)
      );

      loading.value = false;

      const canvasParent = canvasRef.value.parentElement;
      const canvas = new fabric.Canvas(canvasRef.value);

      const width = floorPlan.width;
      const height = floorPlan.height;

      canvas.setWidth(width);
      canvas.setHeight(height);

      const objectScale = width / 833;

      drawDevices(canvas, objectScale, width, height);

      watch(
        () => props.locationDevices,
        () => {
          drawDevices(canvas, objectScale, width, height);
          canvas.renderAll();
        },
        { deep: true }
      );

      const tooltipBackground = new fabric.Rect({
        fill: "#ffffff",
        rx: 8 * objectScale,
        ry: 8 * objectScale,
        originX: "center",
        originY: "center",
        width: 110 * objectScale,
        height: 110 * objectScale,
        shadow: {
          offsetY: 8 * objectScale,
          blur: 15 * objectScale,
          color: "rgb(0 0 0 / 25%)",
        },
      });

      const tooltipText = new fabric.Textbox("", {
        fontFamily: "'Roboto', sans-serif",
        fontSize: 15 * objectScale,
        width: 100 * objectScale,
        textAlign: "center",
        splitByGrapheme: true,
        editable: false,
        strokeWidth: 0,
        originX: "center",
        originY: "center",
        fill: "#000000",
      });

      const tooltip = new fabric.Group([tooltipBackground, tooltipText], {
        width: 110 * objectScale,
        height: 110 * objectScale,
        selectable: false,
        hoverCursor: "normal",
        originX: "center",
        originY: "center",
      });

      canvas.setBackgroundImage(floorPlan, canvas.renderAll.bind(canvas), {});

      scaleCanvas(canvas, width, height, canvasParent.clientWidth / width);

      function showTooltip({
        deviceId,
        left: deviceLeft,
        top: deviceTop,
        originalFontSize,
      }) {
        let top = deviceTop - tooltipBackground.height / 2 - originalFontSize;
        if (top - tooltipBackground.height / 2 <= 0) {
          top = deviceTop + tooltipBackground.height / 2 + originalFontSize;
        }
        const halfTooltip = tooltipBackground.width / 2;
        const left =
          deviceLeft - halfTooltip < 0
            ? halfTooltip
            : deviceLeft + halfTooltip > width
            ? width - halfTooltip
            : deviceLeft;
        tooltip.top = top;
        tooltip.left = left;
        tooltipText.text = props.locationDevices[deviceId].displayName;
        canvas.add(tooltip);
      }

      canvas.on("mouse:over", function ({ target }) {
        if (
          target?.type === deviceIconType &&
          target.deviceId !== props.selectedDeviceId
        ) {
          target.fontSize = target.originalFontSize * 1.5;
          showTooltip(target);
          canvas.renderAll();
        }
      });

      canvas.on("object:moving", function ({ target }) {
        canvas.remove(tooltip);
        if (target?.type === deviceIconType) {
          target.left =
            target.left < 0 ? 0 : target.left > width ? width : target.left;
          target.top =
            target.top < 0 ? 0 : target.top > height ? height : target.top;
          showTooltip(target);
          canvas.renderAll();
        }
      });

      canvas.on("mouse:up", function ({ target }) {
        if (target?.type !== deviceIconType) {
          return;
        }
        if (!props.editable) {
          const deviceId = target.deviceId;
          emit(
            "update:selectedDeviceId",
            deviceId === props.selectedDeviceId ? null : deviceId
          );
          return;
        }
        emit("deviceMoved", {
          deviceId: target.deviceId,
          positionX: target.left,
          positionY: target.top,
        });
      });

      canvas.on("mouse:out", function ({ target }) {
        canvas.remove(tooltip);
        if (
          target?.type === deviceIconType &&
          target.deviceId !== props.selectedDeviceId
        ) {
          target.fontSize = target.originalFontSize;
          canvas.renderAll();
        }
      });

      watch(
        () => props.selectedDeviceId,
        (selectedDeviceId) => {
          if (props.editable) {
            return;
          }
          Object.entries(deviceIcons).forEach(([deviceId, deviceIcon]) => {
            const selected = selectedDeviceId === deviceId;
            deviceIcon.fontSize = selected
              ? deviceIcon.originalFontSize * 2
              : deviceIcon.originalFontSize;
            deviceIcon.fill = selected
              ? mdbPrimary
              : deviceIcon.warningOrDanger;
          });
          canvas.renderAll();
        }
      );

      new ResizeObserver(() =>
        scaleCanvas(canvas, width, height, canvasParent.clientWidth / width)
      ).observe(canvasParent);
    });

    return { loading, canvasRef };
  },
};
</script>

<style>
.canvas-container {
  width: 100% !important;
}
</style>
