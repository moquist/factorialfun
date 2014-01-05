(ns factorialfun.core
  (:gen-class))

(defn f0
  "This is the same as f1, but uses the non-auto-promoting * instead of *'.
   See http://clojuredocs.org/clojure_core/clojure.core/* and
   http://clojuredocs.org/clojure_core/clojure.core/*'
   ArithmeticException integer overflow at (f0 21)"
  [n]
  (println :f0 :n n)
  (if (= 1 n)
    n
    (* n (f0 (dec n)))))

(defn f1
  "This is recursive, but consumes stack.
   Note that the recursive call only passes 'n, because the /result/ of the call is then multiplied by n after the fact.
   This cannot be done with tail-call-optimization (TCO), which is the only way to do recursion without consuming the stack.

   Stack overflow (on my MBA, 8G of RAM) at (f1 8450)"
  [n]
  (println :f1 :n n)
  (if (= 1 n)
    n
    (*' n (f1 (dec n)))))

(defn f2
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

(defn -main
  "Takes in two strings: the name of a fn to call, and the arg for
   that fn.

   Gets the symbol of the first string and resolves it to a namespace.

   Gets the symbol of 'f, rebinds 'f to the symbol value.

   Resolves 'f to the 'factorialfun.core namespace, rebinds 'f to the
   resolved 'f, which should now be a fn from above.

   Ensures that 'f implements IFn, throws exception (with a helpful
   message) if not.

   Calls 'f with 'n, using 'time to print out the elapsed time.

   Finally, prints out the result.

   Returns nil."
  [f-str n]
  (let [f (symbol f-str)
        f (ns-resolve 'factorialfun.core f)
        _ (if (not (ifn? f))
            (throw (Exception. (str f-str " is not a function!"))))
        n (Integer. n)
        factorial (time (f n))]
    (println
     "The factorial of" n "is"
     factorial)))

(comment
  ;; You must test with strings.
  (factorialfun.core/-main "f1" "4")
  (factorialfun.core/-main "f2" "4")
  )
