(ns factorialfun.core
  (:gen-class))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; factorial fns
(defn ^:cli f0
  "This is the same as f1, but uses the non-auto-promoting * instead of *'.
   See http://clojuredocs.org/clojure_core/clojure.core/* and
   http://clojuredocs.org/clojure_core/clojure.core/*'
   ArithmeticException integer overflow at (f0 21)"
  [n]
  (println :f0 :n n)
  (if (= 1 n)
    n
    (* n (f0 (dec n)))))

(defn ^:cli f1
  "This is recursive, but consumes stack.
   Note that the recursive call only passes 'n, because the /result/ of the call is then multiplied by n after the fact.
   This cannot be done with tail-call-optimization (TCO), which is the only way to do recursion without consuming the stack.

   Stack overflow (on my MBA, 8G of RAM) at (f1 8352)"
  [n]
  (println :f1 :n n)
  (if (= 1 n)
    n
    (*' n (f1 (dec n)))))

(defn ^:cli f2
  "Works fine up to (e.g.) 80000, stopped testing.
   \"recur is the only non-stack-consuming looping construct in Clojure.\" (http://clojure.org/special_forms#recur)
   When you use 'recur, you need to re-bind a \"collecting\" var of some kind each time.
   This is what allows you to do the multiplication *before* the recursive call, and this is what makes TCO possible.
   "
  ;; This arity-1 fn takes in our intial n, and makes *one*
  ;; non-TCO recursive call on the stack, to initialize our collecting var.
  ([n]
     (println :f2 :n n)
     (f2 n 1))

  ;; This arity-2 fn takes in our initial n and the initialized
  ;; collecting var "prod". 'recur re-binds n and prod as the
  ;; factorial is calculated, returning the final value of prod when n
  ;; is 1.
  ([n prod]
     (println :f2 :n n :prod prod)
     (if (= 1 n)
       prod
       (recur (dec n) (*' n prod)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; fns to take from seqs named on the CLI.
(defn ^:cli seq-n
  "Convenience fn for testing a sequence of factorials from the CLI.
   The factorial of n is the (dec n) element of the seq."
  [n s]
  (nth s (dec n)))

(defn ^:cli seq-take
  "Convenience fn for testing a sequence of factorials from the CLI."
  [n s]
  (take n s))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; lazy seqs of factorials
(def ^:cli f3-seq
  ;; With apologies to http://en.wikibooks.org/wiki/Clojure_Programming/Examples/Lazy_Fibonacci
  ((fn f3 [prev n]
     (lazy-seq (cons prev (f3 (*' n prev) (inc n)))))
   1 2))

(def ^:cli f4-seq
  ;; Creates a lazy sequence of [position factorial] vectors.
  ;; This sequence is recursive on data.
  ;; With even more apologies to http://en.wikibooks.org/wiki/Clojure_Programming/Examples/Lazy_Fibonacci
   (lazy-cat
    [[1 1] [2 2]]
    (map (fn [[prev-n prev-p]]
           (let [n (inc prev-n)
                 prod (*' n prev-p)]
             [n prod]))
         (rest f4-seq))))

(def ^:cli f5-seq
  ;; Creates a lazy sequence of factorials.
  ;; This sequence is recursive on data.
   (lazy-cat
    [1 2]
    (map-indexed (fn [idx prev]
                   (let [idx (+' 3 idx)
                         next (*' idx prev)]
                     next))
                 (rest f5-seq))))

(defn -main
  "Takes in two strings: the name of a fn to call, and the arg for
   that fn.

   The 'let binding vector here is very convoluted and should raise eyebrows.
   TODO: refactor this
   * try (map while zipping in the results of (range)

   Returns nil."
  [f-str n & [seq-str]]
  (let [;; Get the symbol of the first CLI parameter string.
        f (symbol f-str)

        ;; Resolve the symbol to this namespace.
        f (ns-resolve 'factorialfun.core f)

        ;; If f does not implement IFn, bail out.
        _ (assert (ifn? f))

        ;; Ensure that 'f is marked (according to our own metadata
        ;; convention) to be accessible from the CLI. See
        ;; http://clojure.org/metadata and
        ;; http://stackoverflow.com/questions/5592306/how-do-i-dynamically-find-metadata-for-a-clojure-function
        _ (assert (:cli (meta f)))

        ;; Get the Integer value of the second CLI parameter string.
        n (Integer. n)

        ;; Bind 'f to a fn that already has its first argument.
        f (partial f n)

        ;; If a seq name was passed as the optional third CLI
        ;; parameter, get its symbol, resolve it to this namespace to
        ;; get its var, and then get the value of the var (which is
        ;; the seq itself).
        ;; Then, if 's is a seq and is marked (according to our own
        ;; metadata convention) to be accessible from the CLI (see
        ;; same test above for 'f), bind 'f to an updated partial with
        ;; the seq as an additional argument, and bind 'seq-disp to a
        ;; string describing the seq.
        ;; Else (if no seq name was passed on the CLI), bind 'f to the
        ;; same value and 'seq-disp to the empty string since there's
        ;; nothing here to describe.
        [f seq-disp] (if (string? seq-str)
                       (let [s (symbol seq-str)
                             s-var (ns-resolve 'factorialfun.core s)
                             s (var-get s-var)]
                         (if (and (seq? s)
                                  (:cli (meta s-var)))
                           [(partial f s) (format "(from %s)" seq-str)]
                           (throw (Exception. (str seq-str " is not an available sequence.")))))
                       [f ""])

        ;; Call f and bind the result to 'factorial, using 'time to print out how long it takes.
        factorial (time (f))]
    (println
     (format "%s %s of %d is" f-str seq-disp n)
     factorial)))

(comment
  ;; You must test with strings.
  (factorialfun.core/-main "f0" "4")
  (factorialfun.core/-main "f1" "4")
  (factorialfun.core/-main "f2" "4")
  (factorialfun.core/-main "f2" "4")
  (factorialfun.core/-main "seq-n" "4" "f3-seq")
  (factorialfun.core/-main "seq-take" "4" "f3-seq")
  (factorialfun.core/-main "seq-n" "4" "f4-seq")
  (factorialfun.core/-main "seq-take" "4" "f4-seq")
  )
