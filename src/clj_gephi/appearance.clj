(ns clj-gephi.appearance
  (import [org.openide.util Lookup])
  (import [org.gephi.appearance.api AppearanceController])
  )

(def ac (.lookup (Lookup/getDefault) AppearanceController))

