(ns clj-gephi.project
  (import [org.openide.util Lookup])
  (import [org.gephi.project.api ProjectController Workspace])
  )

(def pc
  (.lookup (Lookup/getDefault) ProjectController))

(defn current-workspace
  []
  (.getCurrentWorkspace pc))

(defn new-workspace!
  [proj]
  (.newWorkspace pc proj))

(defn open-workspace!
  [wp]
  (.openWorkspace pc wp)
  wp)

(defn close-workspace!
  [wp]
  (open-workspace! wp)
  (.closeCurrentWorkspace pc))

(defn delete-workspace!
  [wp]
  (.deleteWorkspace pc wp)
  wp)

(defn current-project
  "return current project"
  [] (.getCurrentProject pc))

(defn new-project!
  "Init a project - and return current project"
  []
  (.newProject pc)
  (current-project))

(defn open-project!
  [proj]
  (.openProject pc proj))

(defn close-project!
  [proj]
  (open-project! proj)
  (.closeCurrentProject pc))

(defn delete-project!
  [proj]
  (.removeProject pc proj))
