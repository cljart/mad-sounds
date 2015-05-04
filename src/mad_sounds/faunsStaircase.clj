(ns mad-sounds.faunsStaircase
  (:require [overtone.live :refer :all]
            [overtone.inst.sampled-piano :refer :all]))

(defn piano-strike [time fingers]
  (let [root (+50 (mod time 24))
        amp (+ 0.3 (/ 0.7 t))]
    (doseq [f (take fingers (chord root :dim7))]
      (sampled-piano f))))
;; let introduces local binding (aka variable) here it is "root" and "amp"

;;doseq introduces local binding; here it is "f" and loops f through sampled piano

;;a chord has 3 to 4 notes and sounds "good"

(defn looper [nome] ;;nome is incoming metronome/timestamp
  (let [beat (nome)
        random-offset (rand 1) ;; for time randomization
        fingers (inc (rand-int 3)) ;; random number between 0 and 4 for fingers
        (at (+ (nome beat) random-offset) (piano-strike beat fingers))
        (apply-by (nome (inc beat)) looper nome []))))

(looper (metronome 220))

(stop)
