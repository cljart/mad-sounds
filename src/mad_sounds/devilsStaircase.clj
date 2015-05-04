(ns mad-sounds.devilsStaircase
  (:require [overtone.live :refer :all]
            [overtone.inst.sampled-piano :refer :all]))

(def amount-fingers 5)

;;(def metro (metronome 100))

(defn audio-level [time finger amount-fingers]
  (cond
    (= finger 0) (/ time 11)
    (= finger (dec amount-fingers)) (- 1 (/ time 11))
    :else 1))

;;volume = amp, octave/frequency, finger/hand, time

;;(defn finger->note [finger time]
;;
;;  (+ 50 (* 12 finger) time))

(defn finger->note [finger time]
 (cond
  (= time (zero? (mod time 3))) (+ 100 (* 12 finger) time)
  (= time (zero? (mod time 2))) (+ 0 (* 12 finger) time)
  :else (+ 50 (* 12 finger) time)))

(defn play-finger [finger amount-fingers]
  (loop [time 0]
    (sampled-piano
     (finger->note finger time)
     (audio-level time finger amount-fingers));;note number
;;     (if (= (rand-int 11) 11)
;;      (java.lang.Thread/sleep 3000)
      (java.lang.Thread/sleep (max 200 (rand-int 500)))
    (if (< time 11);; if time is less than 11
      (recur (inc time))
      (recur 0))))

(dotimes [n amount-fingers]
    (play-finger 0 amount-fingers))
;;define :finger do |finger|
;;  in_thread do
;;    loop do

;; amount_fingers.times do |n|
;;   finger (n)
;; end



(doseq [y (chord 60 :major)] (sampled-piano y))
;;the chord gives us the vector back, do the sequence with y over what chord gives me back. this is like a for loop, so doseq mutates.

(doseq [y (chord 60 :minor)] (sampled-piano y))

(defn play-chord [root chord-name]
  (doseq [y (chord root chord-name)] (sampled-piano y))
)

(stop)
