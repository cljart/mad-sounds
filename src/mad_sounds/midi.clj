(ns mad-sounds.midi
  (:require [fuga.core :as fuga]
            [clojure.java.io :as io]))

(def midi-sequence
  (-> "AMaidensPrayer.mid"
      io/resource ;;gives me path/URL java.net.URL
      io/file ;;gives me file java.io.File
      fuga/read-midi)) ;; gives me Midi sequence javax.sound.midi.Sequence
;;      fuga/extract-tracks) ;; gives me a list of list of events

;;(fuga/read-midi)
;;(fuga/io/resource/AMaidensPrayer.mid)
;;{:tick 39936, :command :ON, :channel 5, :data (34 58)}
;;192 is sequence resolution = 192 ticks = 1 beat

(def metro (metronome 120))

(doseq [note midi-counts]
  (play note (metro))
