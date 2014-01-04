(ns factorialfun.core
  (:gen-class))

(defn f1
  "This is recursive, but consumes stack.
   Note that the recursive call only passes 'n, because the /result/ of the call is then multiplied by n after the fact.
   This cannot be done with tail-call-optimization (TCO), which is the only way to do recursion without consuming the stack.

   Stack overflow (on my MBA) at (f1 8450)"
  [n]
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
  ([n] (f2 n 1))

  ;; This arity-2 fn takes in our initial n and the initialized
  ;; collecting var "prod". 'recur re-binds n and prod as the
  ;; factorial is calculated, returning the final value of prod when n
  ;; is 1.
  ([n prod]
     (if (= 1 n)
       prod
       (recur (dec n) (*' n prod)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
