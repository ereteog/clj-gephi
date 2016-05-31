(ns clj-gephi.preview
  (import [org.gephi.preview.api PreviewController])
  (import [org.openide.util Lookup])
  )

(defn pc []
  (.lookup (Lookup/getDefault) PreviewController))

(defn preview-model
  "PreviewController -> PreviewModel"
  []
  (.getModel pc))

;;model.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
;;model.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.GRAY));
;;model.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, new Float(0.1f));
;;model.getProperties().putValue(PreviewProperty.NODE_LABEL_FONT, model.getProperties().getFontValue(PreviewProperty.NODE_LABEL_FONT).deriveFont(8));

