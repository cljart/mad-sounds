(ns mad-sounds.angelsStaircase
  (:require [overtone.live :refer :all]
            [overtone.inst.sampled-piano :refer :all]
            [quil.core :as q]))

; setup a tempo for our metronome to use
(def metro (metronome 120))

(def notes-played (atom []))

(defn strike-notes [time amount-fingers]
  (let [current-time (mod time 12)
        finger-one (+ 70 current-time)
        all-fingers (mapv #(+ % finger-one) (range amount-fingers))]; vector containing all fingers with all notes
    (reset! notes-played all-fingers) ;;reset all notes to all fingers
    (doseq [finger all-fingers] ;plays all fingers
      (sampled-piano finger))));; puts current finger into sampled piano


; This Function will play our sound at whatever tempo we've set our metronome to
(defn looper [nome]
    (let [beat (nome)
          fingers (inc (rand-int 5))]
        ;; schedules a specific number of piano notes at this beat
        (at (nome beat) (strike-notes beat fingers))
        ;;
        ;; schedules looper to be called at metronome + the next beat
        (apply-by (nome (inc beat)) looper nome [])))

; turn on the metronome
(looper metro)
(stop)

(defn draw []
  ; make background white; 255 is the color white
  (q/background 255)
  (q/fill 255 0 0)
  (let [keys-played (mapv #(mod % 12) @notes-played)];; maps notes (80, 81) to keys (0-11)
    (dotimes [n 12] ;draw from 0 to 7 rectangles, n is index
      (if (some #(= % n) keys-played);; if note is in the list of piano keys
        (q/fill 255);; key is white when played
        (q/fill (/ 255 (inc n ))));; otherwise key is normal gradient color
      (q/rect (* n (/ 600 12)) 0 (/ 600 12) 200))
    (q/text (str @notes-played) 10 10))
)

;call defsketch macro to draw a window
(q/defsketch angelsStaircase
  :size [600 200] ;pixel size of window size x and y
  :draw draw)
