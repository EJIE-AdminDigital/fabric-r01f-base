package r01f.patterns;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import jakarta.validation.constraints.NotNull;
import r01f.types.Empty;

/**
 * #see https://github.com/xyzsd/dichotomy
 *
 * A right-biased Either monad implementation.
 *	- An {@link Either} holds Left or Right values
 * 	- ... but never both or neither
 * 	- ... otherwise it'd be a tuple rather than an {@link Either}
 *
 * Features:
 *	- {@link Left} and {@link Right} values cannot hold {@code null}. The {@link Empty} type can be used instead
 *	- Right-biased: methods whose name is not ending in {@code Left} operate on {@link Right} values.
 *	  However, the {@code xxxLeft} methods are provided so that a {@link #swap}() is not required.
 *	- Sealed: can be used in {@code switch} statements and deconstruction patterns
 *	- Separable: this file only depends on standard JDK classes
 *
 * @param <L> The left-hand value (by convention, this is the failure/unsuccessful value)
 * @param <R> The right-hand value (by convention, the success value)
 */
public sealed interface Either<L,R> {
/////////////////////////////////////////////////////////////////////////////////////////
//	BUILDERS
/////////////////////////////////////////////////////////////////////////////////////////
	static <L,R> Either<L,R> ofLeft(@NotNull final L value) {
		return new Left<>(value);
	}
	@NotNull
	static <L,R> Either<L,R> ofRight(@NotNull final R value) {
		return new Right<>(value);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns {@code true} is this is a {@link Left} {@link Either}.
	 * @return true if this is a Left Either.
	 */
	boolean isLeft();
	/**
	 * Returns {@code true} is this is a {@link Right} {@link Either}.
	 * @return true if this is a Right Either.
	 */
	boolean isRight();
////////// RETRIEVE
	/**
	 * If the {@link Either} is {@link Left}, return the value as an {@link Optional}.
	 * Otherwise, return an empty {@link Optional}.
	 * @return Left value or Optional.Empty if this is Right
	 */
	@NotNull Optional<L> left();
	/**
	 * If the {@link Either} is {@link Right}, return the value as an {@link Optional}.
	 * Otherwise, return an empty {@link Optional}.
	 * @return Right value or Optional.Empty if this is Left
	 */
	@NotNull Optional<R> right();
	/**
	 * If this {@link Either} is {@link Left}, return {@code rightAlternate}.
	 * Otherwise, return {@code this} (a {@link Right} {@link Either}).
	 *
	 * @param rightAlternate alternate {@link Right} {@link Either}
	 * @return {@code this}, or {@code rightAlternate} if {@code this} is {@link Left}
	 * @see #orElseGet
	 * @see #orElseLeft
	 * @see #orElseGetLeft
	 */
	@NotNull R orElse(@NotNull R rightAlternate);
	/**
	 * If this {@link Either} is {@link Right}, return {@code leftAlternate}.
	 * Otherwise, return {@code this} (a {@link Left} {@link Either}).
	 *
	 * @param leftAlternate alternate {@link Left} {@link Either}
	 * @return {@code this}, or {@code leftAlternate} if {@code this} is {@link Right}
	 * @see #orElseLeft
	 * @see #orElse
	 * @see #orElseGet
	 */
	@NotNull L orElseLeft(@NotNull L leftAlternate);
	/**
	 * If this {@link Either} is {@link Left}, return the supplied {@link Right} {@link Either}.
	 * Otherwise, return {@code this} (a {@link Right} {@link Either}) without
	 * invoking the {@link Supplier}.
	 *
	 * @param rightSupplier supplier of {@link Right} {@link Either}s
	 * @return {@code this}, or the supplied {@link Right} {@link Either} if {@code this} is {@link Left}
	 * @see #orElse
	 * @see #orElseLeft
	 * @see #orElseGetLeft
	 */
	@NotNull R orElseGet(@NotNull Supplier<? extends R> rightSupplier);
	/**
	 * If this {@link Either} is {@link Right}, return the supplied {@link Left}  {@link Either}.
	 * Otherwise, return {@code this} (a {@link Left} {@link Either}) without
	 * invoking the {@link Supplier}.
	 *
	 * @param leftSupplier supplier of {@link Left} {@link Either}s
	 * @return {@code this}, or the supplied {@link Left} {@link Either} if {@code this} is {@link Right}
	 * @see #orElseLeft
	 * @see #orElse
	 * @see #orElseGet
	 */
	@NotNull L orElseGetLeft(@NotNull Supplier<? extends L> leftSupplier);
	/**
	 * Return the {@link Right} value if this is a {@link Right} {@link Either} without invoking the {@link Supplier}.
	 * Otherwise, throw the supplied {@link Exception}.
	 *
	 * If the supplied {@link Exception} extends {@link RuntimeException}, this method does not
	 * need to be enclosed within a {@code try } block.
	 *
	 * @param supplier {@link Exception} {@link Supplier}
	 * @param <E>	  {@link Exception}
	 * @return {@link Right} value, if present
	 * @throws X					the supplied {@link Exception}
	 * @throws NullPointerException if the called Supplier returns {@code null}.
	 */
	@NotNull <E extends Throwable> R expect(@NotNull Supplier<E> supplier) throws E;
	/**
	 * Return the {@link Right} value if this is a {@link Right} {@link Either} without invoking the {@link Function}
	 * supplying the exception.
	 * Otherwise, invoke the {@link Function} to produce the {@link Exception}.
	 *
	 * If the produced {@link Exception} extends {@link RuntimeException}, this method does not
	 * need to be enclosed within a {@code try } block.
	 *
	 * This method is particularly useful to convert Exception types, or to create Exceptions with
	 * value messages dependent upon the {@link Left} value.
	 *
	 * For example:
	 * {@snippet :
	 *			  Either<String,Integer> int anExampleMethod(int input) {
	 *				  if (input > 100) {
	 *					 return Either.ofRight(input * 10);
	 *				  } else {
	 *					 return Either.ofLeft("input too small");
	 *				  }
	 *			  }
	 *
	 *			  Either<String,Integer> myEitherGood = anExampleMethod(1000);
	 *			  int goodValue = myEitherGood.getOrThrow(ArithmeticException::new); // goodValue = 10000; no exception thrown
	 *
	 *			  // regarding getOrThrow(ArithmeticException::new)
	 *			  // this works because there is an ArithmeticException(String) constructor;
	 *			  // otherwise a compile-time value would occur (no appropriate constructor to match)
	 *			  Either<String,Integer> myEitherBad = anExampleMethod(-37234);
	 *			  int badValue = myEitherGood.getOrThrow(ArithmeticException::new); // throws a new ArithmeticException("input too small")
	 * }
	 * To create an exception that does <b>not</b> wrap:
	 * {@snippet :
	 *	  getOrThrow((x) -> new IOException("My Exception"));
	 * }
	 * </p>
	 *
	 * @param exFn {@link Exception} producing {@link Function}
	 * @param <E>  {@link Exception}
	 * @return {@link Right} value, if present
	 * @throws X					the produced {@link Exception}
	 */
	@NotNull <E extends Throwable> R getOrThrow(@NotNull Function<L,E> exFn) throws E;
	/**
	 * Returns a value, produced from one of the appropriate mapping functions.
	 * The produced value can have any type (except {@link Void}) but mapping functions for
	 * both {@link Left} and {@link Right} types must produce the same value type.
	 *
	 * If no value is to be returned, use {@link #biMatch(Consumer,Consumer)} instead.
	 *
	 * @param fnLeft  the mapping function for {@link Left} values.
	 * @param fnRight the mapping function for {@link Right} values.
	 * @param <T> common type returned by mapping functions.
	 * @return the value produced from {@code fnLeft} or {@code fnRight}
	 * @see #recover(Function)
	 * @see #forfeit(Function)
	 */
	@NotNull <T> T fold(@NotNull Function<? super L,? extends T> fnLeft,
						@NotNull Function<? super R,? extends T> fnRight);
	/**
	 * Forfeit (ignore) the {@link Right} value if present, and apply the mapping function to get a {@link Left}.
	 *
	 * If this is a {@link Left}, return it without applying the mapping function.
	 *
	 * This method is equivalent in alternative implementations to {@code orElseMapLeft()}.
	 *
	 * @param fn {@link Function} that produces a {@link Left} value.
	 * @return A {@link Left} value; the current {@link Left} if present, or the produced {@link Left} if not.
	 * @see #recover(Function)
	 */
	@NotNull L forfeit(@NotNull Function<? super R,? extends L> fn);
	/**
	 * Recover from a value; ignore the {@link Left} value if present,
	 * and apply the mapping function to get a {@link Right}.
	 *
	 * If this is a {@link Right}, return it without applying the mapping function.
	 *
	 * This method is equivalent in alternative implementations to {@code orElseMap()}.
	 *
	 * @param fn {@link Function} that produces a {@link Right} value.
	 * @return A {@link Right} value; the current {@link Right} if present, or the produced {@link Right} if not.
	 * @see #forfeit(Function)
	 */
	@NotNull R recover(@NotNull Function<? super L,? extends R> fn);

////////// COMPOSITION
	/**
	 * If {@code this} is {@link Left}, return it. Otherwise, return the next {@link Either} given.
	 * The next Either can have a different parameterized Right type.
	 *
	 * @param nextEither The {@link Either} to return.
	 * @param <R2> right value type, which may be different from the original type
	 * @see #and(Supplier)
	 * @see #or(Either)
	 * @see #or(Supplier)
	 * @return this or nextEither
	 */
	@NotNull <R2> Either<L,R2> and(@NotNull Either<L,R2> nextEither);
	/**
	 * If {@code this} is {@link Left}, return it (without invoking the {@link Supplier}).
	 * Otherwise, return the next {@link Either} supplied.
	 * The next Either can have a different parameterized Right type.
	 *
	 * @param nextEitherSupplier The supplier of an {@link Either} to return; only called if {@code this} is {@link Right}.
	 * @param <R2>			   New {@link Right} value
	 * @throws NullPointerException if the supplied {@link Either} is {@code null}.
	 * @see #and(Either)
	 * @see #or(Either)
	 * @see #or(Supplier)
	 * @return this or nextEither
	 */
	@NotNull <R2> Either<L,R2> and(@NotNull Supplier<Either<L,R2>> nextEitherSupplier);
	/**
	 * If {@code this} is {@link Right}, return it.
	 * Otherwise, return the next {@link Either} given.
	 * The next Either can have a different parameterized Left type.
	 *
	 * @param nextEither The {@link Either} to return.
	 * @param <L2>	   New {@link Left} value
	 * @see #or(Supplier)
	 * @see #and(Either)
	 * @see #and(Supplier)
	 * @return this or nextEither
	 */
	@NotNull <L2> Either<L2,R> or(@NotNull Either<L2,R> nextEither);
	/**
	 * If {@code this} is {@link Right}, return it (without invoking the {@link Supplier}).
	 * Otherwise, return the next {@link Either} supplied.
	 * The next Either can have a different parameterized Left type.
	 *
	 * @param nextEitherSupplier The supplier of an {@link Either} to return; only called if {@code this} is {@link Left}.
	 * @param <L2>			   New {@link Left} value
	 * @throws NullPointerException if the supplier is called and returns {@code null}.
	 * @see #or(Either)
	 * @see #and(Either)
	 * @see #and(Supplier)
	 * @return this or nextEither
	 */
	@NotNull <L2> Either<L2,R> or(@NotNull Supplier<Either<L2,R>> nextEitherSupplier);

////////// CONTAINS / MATCHS
	/**
	 * Determines if this {@link Right} {@link Either} contains the given value.
	 * This will always return {@code false} for {@code null} values.
	 *
	 * @param rVal value to compare
	 * @return {@code true} iff this is a {@link Right} {@link Either} and the contained value equals {@code rVal}
	 * @see #containsLeft
	 */
	boolean contains(@Nullable R rVal);
	/**
	 * Determines if this {@link Left} {@link Either} contains the given value.
	 * This will always return {@code false} for {@code null} values.
	 *
	 * @param lVal value to compare
	 * @return {@code true} iff this is a {@link Left} {@link Either} and the contained value equals {@code lVal}
	 * @see #contains
	 */
	boolean containsLeft(@Nullable L lVal);
	/**
	 * Determines if this {@link Right} {@link Either} matches the given {@link Predicate}.
	 * The {@link Predicate} is not tested if this is a {@link Left} {@link Either}
	 *
	 * @param rp the {@link Predicate} to test
	 * @return {@code true} iff this is a {@link Right} {@link Either} and the {@link Predicate} matches.
	 * @see #contains
	 * @see #containsLeft
	 */
	boolean rightMatches(@NotNull Predicate<R> rp);
	/**
	 * Determines if this {@link Left} {@link Either} matches the given {@link Predicate}.
	 * The {@link Predicate} is not tested if this is a {@link Right} {@link Either}
	 *
	 * @param lp the {@link Predicate} to test
	 * @return {@code true} iff this is a {@link Left} {@link Either} and the {@link Predicate} matches.
	 * @see #containsLeft
	 * @see #contains
	 */
	boolean leftMatches(@NotNull Predicate<L> lp);
////////// STREAM
	/**
	 * Return a {@link Stream}, containing either a single {@link Right} value, or an empty {@link Stream}
	 * if this is a {@link Left value}.
	 *
	 * @return Stream
	 * @see #streamLeft()
	 */
	@NotNull Stream<R> stream();
	/**
	 * Return a {@link Stream}, containing either a single {@link Left} value, or an empty {@link Stream}
	 * if this is a {@link Right value}.
	 *
	 * @see #stream()
	 * @return Stream
	 */
	@NotNull Stream<L> streamLeft();
	/**
	 * Create a new {@link Either} with the left and right values transposed.
	 * This can be used to effectively change the bias.
	 *
	 * For example, the following are equivalent:
	 * {@snippet :
	 *	  myEither.flatMapLeft(MyFunction::doIt) == myEither.swap().flatMap(MyFunction::doIt);
	 * }
	 *
	 * @return A new {@link Either} with left and right values swapped.
	 */
	@NotNull Either<R,L> swap();
	/**
	 * Filter an {@link Either}.
	 *
	 * If this {@link Either} is {@link Left}, return {@link Left} ({@code this}).
	 * The {@code Predicate} is not tested, and the mapper {@code Function} is not executed.
	 *
	 * If this {@link Either} is {@link Right}, return {@link Right} ({@code this}) if the {@code Predicate} matches.
	 * If the {@code Predicate} fails to match, return a {@link Left} {@link Either} produced by applying the
	 * mapping function to the current {@link Either} ({@code this}).
	 *
	 * @param predicate the predicate used to test {@link Right} values.
	 * @param mapper the mapping function for {@link Right} values that do not match the predicate.
	 * @return an {@link Either} based on the algorithm described above.
	 */
	@NotNull Either<L,R> filter(@NotNull Predicate<? super R> predicate,
								@NotNull Function<? super R,? extends L> mapper);
////////// MAP
	/**
	 * If this is a {@link Right}, return a new {@link Right} value produced by the given mapping function.
	 * Otherwise, return the {@link Left} value.
	 * The type of the produced {@link Right} can be different. The mapping function is only invoked for
	 * {@link Right} values.
	 * This is equivalent to {@code biMap( Function.identity(), rightMapper )}.
	 *
	 * @param rightMapper the mapping function producing a new {@link Right} value.
	 * @param <R2> right value type, which may be different from the original type
	 * @return a new {@link Right} produced by the mapping function if this is {@link Right}; otherwise, returns a {@link Left}.
	 * @see #mapLeft(Function)
	 * @see #biMap(Function, Function)
	 */
	@NotNull <R2> Either<L,R2> map(@NotNull Function<? super R,? extends R2> rightMapper);
	/**
	 * If this is a {@link Right}, return the new {@link Either} supplied by the mapping function.
	 * Note that while the {@link Left} type must remain the same, the {@link Right} type returned
	 * can be different.
	 * This is also known as {@code join()} in other implementations.
	 * No mapping is performed if this is a {@link Left}, and the mapping function is not invoked.
	 *
	 * @param rightMapper the mapping function that produces a new {@link Either}
	 * @param <R2> right value type, which may be different from the original type
	 * @return a new {@link Right} produced by the mapping function if this is {@link Right}; otherwise, returns a {@link Left}.
	 * @see #biFlatMap(Function, Function)
	 * @see #flatMapLeft(Function)
	 */
	@NotNull <R2> Either<L,R2> flatMap(@NotNull Function<? super R, ? extends Either<? extends L,? extends R2>> rightMapper);
	/**
	 * If this is a {@link Left}, return a new {@link Left} value produced by the given mapping function.
	 * Otherwise, return the {@link Right} value.
	 * The type of the produced {@link Left} can be different. The mapping function is only invoked for
	 * {@link Left} values.
	 * This is equivalent to {@code map( leftMapper, Function.identity() )}.
	 *
	 * @param leftMapper the mapping function producing a new {@link Left} value.
	 * @param <L2> left value type, which may be different from the original type
	 * @return a new {@link Left} produced by the mapping function if this is {@link Left}; otherwise, returns a {@link Right}.
	 * @see #map(Function)
	 * @see #biMap(Function, Function)
	 */
	@NotNull <L2> Either<L2,R> mapLeft(@NotNull Function<? super L,? extends L2> leftMapper);
	/**
	 * If this is a {@link Left}, return the new {@link Either} supplied by the mapping function.
	 * Note that while the {@link Right} type must remain the same, the {@link Left} type returned
	 * can be different.
	 * This is also known as a left-{@code join()} in other implementations.
	 * No mapping is performed if this is a {@link Right}, and the mapping function is not invoked.
	 *
	 * @param leftMapper the mapping function that produces a new {@link Either}
	 * @param <L2> left value type, which may be different from the original type
	 * @return a new {@link Left} produced by the mapping function if this is {@link Left}; otherwise, returns a {@link Right}.
	 * @see #biFlatMap(Function, Function)
	 * @see #flatMap(Function)
	 */
	@NotNull <L2> Either<L2,R> flatMapLeft(@NotNull Function<? super L,? extends Either<? extends L2,? extends R>> leftMapper);
	/**
	 * Returns a new {@link Either}, the value of which is determined by the appropriate mapping function.
	 * The returned Either (which may be {@link Left} or {@link Right}) can have different types.
	 *
	 * @param fnLeft  the mapping function for {@link Left} values.
	 * @param fnRight the mapping function for {@link Right} values.
	 * @param <R2> right value type, which may be different from the original type
	 * @param <L2> left value type, which may be different from the original type
	 * @return the {@link Either} produced from {@code fnLeft} or {@code fnRight}
	 * @see #map(Function)
	 * @see #mapLeft(Function)
	 */
	@NotNull <L2,R2> Either<L2,R2> biMap(@NotNull Function<? super L,? extends L2> fnLeft,
										 @NotNull Function<? super R,? extends R2> fnRight);
	/**
	 * Returns an {@link Either}, produced from one of the appropriate mapping functions.
	 * The produced Either (which may be {@link Left} or {@link Right}) can have different types.
	 *
	 * @param fnLeft  the mapping function for {@link Left} values.
	 * @param fnRight the mapping function for {@link Right} values.
	 * @param <L2>	New {@link Left} value
	 * @param <R2>	New {@link Right} value
	 * @return the {@link Either} produced from {@code fnLeft} or {@code fnRight}
	 * @see #map(Function)
	 * @see #mapLeft(Function)
	 */
	@NotNull <L2, R2> Either<L2,R2> biFlatMap(@NotNull Function<? super L,? extends Either<? extends L2,? extends R2>> fnLeft,
											  @NotNull Function<? super R,? extends Either<? extends L2,? extends R2>> fnRight);
////////// MATCH
	/**
	 * Executes the action if this is a {@link Left} {@link Either}.
	 *
	 * @param leftConsumer the consumer function to be executed
	 * @return {@code this}
	 * @throws NullPointerException if the called action returns {@code null}.
	 * @see #match(Consumer)
	 * @see #biMatch(Consumer, Consumer)
	 */
	@NotNull Either<L,R> matchLeft(@NotNull Consumer<? super L> leftConsumer);
	/**
	 * Executes the action if this is a {@link Right} {@link Either}.
	 *
	 * @param rightConsumer the consumer function to be executed
	 * @return {@code this}
	 * @throws NullPointerException if the called action returns {@code null}.
	 * @see #match(Consumer)
	 * @see #biMatch(Consumer, Consumer)
	 */
	@NotNull Either<L,R> match(@NotNull Consumer<? super R> rightConsumer);
	/**
	 * Executes the action for the {@link Left} or {@link Right} depending upon
	 * the value of this {@link Either}.
	 *
	 * @return {@code this}
	 * @throws NullPointerException if any argument is null, or if the called action returns {@code null}.
	 * @param leftConsumer consumer executed if this is an {@code Either.Left}
	 * @param rightConsumer consumer executed if this is an {@code Either.Right}
	 * @see #match(Consumer)
	 * @see #matchLeft(Consumer)
	 */
	@NotNull Either<L,R> biMatch(@NotNull Consumer<? super L> leftConsumer,
								 @NotNull Consumer<? super R> rightConsumer);
	/**
	 * Executes the given consumer if this is a {@link Right}.
	 * This is a terminal operation.
	 *
	 * @param rightConsumer the consumer function to be executed
	 */
	default void consume(@NotNull final Consumer<? super R> rightConsumer) {
		this.match(rightConsumer );
	}

/////////////////////////////////////////////////////////////////////////////////////////
//	LEFT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Implementation of a Left {@link Either}.
	 *
	 * @param value Left value. May not be {@code null}.
	 * @param <L>   parameter type of Left values.
	 * @param <R>   parameter type of Right values.
	 */
		record Left<L,R>(@NotNull L value)
	implements Either<L,R> {
		public Left {
			Objects.requireNonNull(value,"Left: value cannot be null!" );
		}

		////////// IS
		@Override
		public boolean isLeft() {
			return true;
		}
		@Override
		public boolean isRight() {
			return false;
		}
		////////// Retrieve
		@NotNull
		public L get() {
			return value;
		}
		@Override
		public @NotNull Optional<L> left() {
			return Optional.of(value);
		}
		@Override
		public @NotNull Optional<R> right() {
			return Optional.empty();
		}
		@Override
		public @NotNull R orElse(@NotNull final R rightAlternate) {
			Objects.requireNonNull(rightAlternate);
			return rightAlternate;
		}
		@Override
		public @NotNull L orElseLeft(@NotNull final L leftAlternate) {
			Objects.requireNonNull(leftAlternate);
			return value;
		}
		@Override
		public @NotNull R orElseGet(@NotNull final Supplier<? extends R> rightSupplier) {
			Objects.requireNonNull(rightSupplier);
			return Objects.requireNonNull(rightSupplier.get());
		}
		@Override
		public @NotNull L orElseGetLeft(@NotNull final Supplier<? extends L> leftSupplier) {
			Objects.requireNonNull(leftSupplier);
			return value;
		}
		@Override
		public <E extends Throwable> @NotNull R expect(@NotNull final Supplier<E> supplier) throws E {
			Objects.requireNonNull(supplier);
			throw requireNonNull(supplier.get());
		}
		@Override
		public <E extends Throwable> @NotNull R getOrThrow(@NotNull final Function<L,E> exFn) throws E {
			Objects.requireNonNull(exFn);
			throw Objects.requireNonNull(exFn.apply(value));
		}
		@Override
		public @NotNull L forfeit(@NotNull final Function<? super R,? extends L> fn) {
			Objects.requireNonNull(fn);
			return value;
		}
		@Override
		public @NotNull R recover(@NotNull final Function<? super L,? extends R> fn) {
			Objects.requireNonNull( fn );
			return Objects.requireNonNull(fn.apply(value));
		}
		@Override
		public <T> @NotNull T fold(@NotNull final Function<? super L,? extends T> fnLeft,
								   @NotNull final Function<? super R,? extends T> fnRight) {
			Objects.requireNonNull(fnLeft);
			Objects.requireNonNull(fnRight);
			return Objects.requireNonNull(fnLeft.apply(value));
		}
		////////// Composition
		@Override
		public @NotNull <L2> Either<L2, R> or(@NotNull final Supplier<Either<L2, R>> nextEitherSupplier) {
			Objects.requireNonNull(nextEitherSupplier);
			return Objects.requireNonNull(nextEitherSupplier.get());
		}
		@Override
		public <R2> @NotNull Either<L,R2> and(@NotNull final Either<L,R2> nextEither) {
			Objects.requireNonNull(nextEither);
			return _coerce();
		}
		// more efficient than implementation: does not invoke supplier
		@Override
		public <R2> @NotNull Either<L,R2> and(@NotNull final Supplier<Either<L,R2>> nextEitherSupplier) {
			Objects.requireNonNull(nextEitherSupplier);
			return _coerce();
		}
		@Override
		public <L2> @NotNull Either<L2,R> or(@NotNull final Either<L2,R> nextEither) {
			Objects.requireNonNull(nextEither);
			return nextEither;
		}
		////////// Value
		@Override
		public boolean contains(@Nullable final R rVal) {
			return false;
		}
		@Override
		public boolean containsLeft(@Nullable final L lVal) {
			return value.equals(lVal);
		}
		@Override
		public boolean rightMatches(@NotNull final Predicate<R> rp) {
			return false;
		}
		@Override
		public boolean leftMatches(@NotNull final Predicate<L> lp) {
			return Objects.requireNonNull(lp)
						  .test(value);
		}
		////////// Stream
		@Override
		public @NotNull Stream<R> stream() {
			return Stream.empty();
		}
		@Override
		public @NotNull Stream<L> streamLeft() {
			return Stream.of(value);
		}
		@Override
		public @NotNull Either<L,R> filter(@NotNull final Predicate<? super R> predicate,
										   @NotNull final Function<? super R,? extends L> mapper) {
			Objects.requireNonNull(predicate);
			Objects.requireNonNull(mapper);
			return this;
		}
		@Override
		public @NotNull Either<R, L> swap() {
			return Either.ofRight( value );
		}
		////////// Map
		@Override
		public @NotNull <R2> Either<L,R2> map(@NotNull final Function<? super R,? extends R2> rightMapper) {
			Objects.requireNonNull(rightMapper);
			return _coerce();
		}
		@Override
		public @NotNull <L2> Either<L2,R> mapLeft(@NotNull final Function<? super L,? extends L2> leftMapper) {
			Objects.requireNonNull(leftMapper);
			return Either.ofLeft(leftMapper.apply(value));
		}
		@Override
		public @NotNull Either<L,R> matchLeft(@NotNull final Consumer<? super L> leftConsumer) {
			Objects.requireNonNull(leftConsumer);
			leftConsumer.accept(value);
			return this;
		}
		@Override @SuppressWarnings("unchecked")
		public <L2> @NotNull Either<L2, R> flatMapLeft(@NotNull final Function<? super L,? extends Either<? extends L2,? extends R>> leftMapper) {
			Objects.requireNonNull(leftMapper);
			return (Either<L2,R>)Objects.requireNonNull(leftMapper.apply(value));
		}
		@Override
		public <R2> @NotNull Either<L, R2> flatMap(@NotNull final Function<? super R,? extends Either<? extends L,? extends R2>> rightMapper) {
			Objects.requireNonNull(rightMapper);
			return _coerce();
		}
		@Override
		public <L2,R2> @NotNull Either<L2,R2> biMap(@NotNull final Function<? super L,? extends L2> fnLeft,
													@NotNull final Function<? super R,? extends R2> fnRight) {
			Objects.requireNonNull(fnLeft);
			Objects.requireNonNull(fnRight);
			return Either.ofLeft(fnLeft.apply(value));
		}
		@Override @SuppressWarnings("unchecked")
		public <L2,R2> @NotNull Either<L2,R2> biFlatMap(@NotNull final Function<? super L,? extends Either<? extends L2,? extends R2>> fnLeft,
														@NotNull final Function<? super R,? extends Either<? extends L2,? extends R2>> fnRight) {
			Objects.requireNonNull(fnLeft);
			Objects.requireNonNull(fnRight);
			return (Either<L2,R2>)Objects.requireNonNull(fnLeft.apply(value));
		}
		////////// Match
		@Override
		public @NotNull Either<L,R> biMatch(@NotNull final Consumer<? super L> leftConsumer,
											@NotNull final Consumer<? super R> rightConsumer) {
			Objects.requireNonNull(leftConsumer);
			Objects.requireNonNull(rightConsumer);

			leftConsumer.accept(value);
			return this;
		}
		@Override
		public @NotNull Either<L, R> match(@NotNull final Consumer<? super R> rightConsumer) {
			requireNonNull( rightConsumer );
			return this;
		}
		////////// Util
		/**
		 * Coerce the empty Right parameter to the new type.
		 * Does not create a new object.
		 *
		 * @param <R2> new Right parameter
		 * @return coerced Left
		 */
		@NotNull @SuppressWarnings("unchecked")
		private <R2> Either<L,R2> _coerce() {
			return (Either<L,R2>)this;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	RIGHT
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Implementation of a Right {@link Either}.
	 *
	 * @param value Right value. May not be {@code null}.
	 * @param <L>   parameter type of Left values.
	 * @param <R>   parameter type of Right values.
	 */
	record Right<L,R>(@NotNull R value) implements Either<L,R> {
		public Right {
			Objects.requireNonNull(value,"Right: value cannot be null!");
		}
		////////// IS
		@Override
		public boolean isLeft() {
			return false;
		}
		@Override
		public boolean isRight() {
			return true;
		}
		////////// Get
		@NotNull
		public R get() {
			return this.value;
		}
		@Override
		public @NotNull Optional<L> left() {
			return Optional.empty();
		}
		@Override
		public @NotNull Optional<R> right() {
			return Optional.of(value);
		}
		@Override
		public @NotNull R orElse(@NotNull final R rightAlternate) {
			Objects.requireNonNull(rightAlternate);
			return value;
		}
		@Override
		public @NotNull L orElseLeft(@NotNull final L leftAlternate) {
			Objects.requireNonNull(leftAlternate);
			return leftAlternate;
		}
		@Override
		public @NotNull R orElseGet(@NotNull final Supplier<? extends R> rightSupplier) {
			Objects.requireNonNull(rightSupplier);
			return value;
		}
		@Override
		public @NotNull L orElseGetLeft(@NotNull final Supplier<? extends L> leftSupplier) {
			Objects.requireNonNull(leftSupplier);
			return Objects.requireNonNull( leftSupplier.get() );
		}
		@Override
		@NotNull
		public <X extends Throwable> R expect(@NotNull final Supplier<X> supplier)  {
			// supplier not invoked
			Objects.requireNonNull(supplier);
			return value;
		}
		@Override
		public <X extends Throwable> @NotNull R getOrThrow(@NotNull final Function<L,X> exFn) {
			// function not invoked
			Objects.requireNonNull(exFn);
			return value;
		}
		@Override
		public <T> @NotNull T fold(@NotNull final Function<? super L,? extends T> fnLeft,
								   @NotNull final Function<? super R,? extends T> fnRight) {
			Objects.requireNonNull(fnLeft);
			Objects.requireNonNull(fnRight);
			return Objects.requireNonNull(fnRight.apply(value));
		}
		@Override
		public @NotNull L forfeit(@NotNull final Function<? super R,? extends L> fn) {
			Objects.requireNonNull(fn);
			return Objects.requireNonNull(fn.apply(value));
		}
		@Override
		public @NotNull R recover(@NotNull final Function<? super L,? extends R> fn) {
			Objects.requireNonNull(fn);
			return value;
		}
		////////// Composition
		@Override
		public <R2> @NotNull Either<L,R2> and(@NotNull final Either<L,R2> nextEither) {
			Objects.requireNonNull(nextEither);
			return nextEither;
		}
		@Override
		public <L2> @NotNull Either<L2,R> or(@NotNull final Either<L2,R> nextEither) {
			Objects.requireNonNull(nextEither);
			return _coerce();
		}
		// more efficient than  implementation : does not invoke supplier
		@Override
		public <L2> @NotNull Either<L2,R> or(@NotNull final Supplier<Either<L2,R>> nextEitherSupplier) {
			Objects.requireNonNull( nextEitherSupplier );
			return _coerce();
		}
		@Override
		public @NotNull <R2> Either<L,R2> and(@NotNull final Supplier<Either<L,R2>> nextEitherSupplier) {
			Objects.requireNonNull(nextEitherSupplier);
			return Objects.requireNonNull(nextEitherSupplier.get());
		}
		////////// Values
		@Override
		public boolean contains(@Nullable final R rVal) {
			return value.equals(rVal);
		}
		@Override
		public boolean containsLeft(@Nullable final L lVal) {
			return false;
		}
		@Override
		public boolean rightMatches(@NotNull final Predicate<R> rp) {
			return Objects.requireNonNull(rp)
						  .test(value);
		}
		@Override
		public boolean leftMatches(@NotNull final Predicate<L> lp) {
			Objects.requireNonNull(lp);
			return false;
		}
		////////// Stream
		@Override
		public @NotNull Stream<R> stream() {
			return Stream.of(value);
		}
		@Override
		public @NotNull Stream<L> streamLeft() {
			return Stream.empty();
		}
		@Override
		public @NotNull Either<L,R> filter(@NotNull final Predicate<? super R> predicate,
										   @NotNull final Function<? super R,? extends L> mapper) {
			Objects.requireNonNull(predicate);
			Objects.requireNonNull(mapper);
			return predicate.test(value) ? this
										 : Either.ofLeft(mapper.apply(value)); // implicit null check
		}
		@Override
		public @NotNull Either<R,L> swap() {
			return ofLeft(value);
		}
		////////// Map
		@Override
		public <L2> @NotNull Either<L2,R> flatMapLeft(@NotNull final Function<? super L,? extends Either<? extends L2,? extends R>> leftMapper) {
			Objects.requireNonNull(leftMapper);
			return _coerce();
		}
		@Override @SuppressWarnings("unchecked")
		public <R2> @NotNull Either<L,R2> flatMap(@NotNull final Function<? super R,? extends Either<? extends L,? extends R2>> rightMapper) {
			Objects.requireNonNull( rightMapper );
			return (Either<L,R2>)Objects.requireNonNull(rightMapper.apply(value));
		}
		@Override
		public <L2,R2> @NotNull Either<L2,R2> biMap(@NotNull final Function<? super L,? extends L2> fnLeft,
													@NotNull final Function<? super R,? extends R2> fnRight) {
			Objects.requireNonNull(fnLeft);
			Objects.requireNonNull(fnRight);
			return Either.ofRight(fnRight.apply(value));
		}
		@Override @SuppressWarnings("unchecked")
		public <L2,R2> @NotNull Either<L2,R2> biFlatMap(@NotNull final Function<? super L,? extends Either<? extends L2,? extends R2>> fnLeft,
														@NotNull final Function<? super R,? extends Either<? extends L2,? extends R2>> fnRight) {
			Objects.requireNonNull(fnLeft);
			Objects.requireNonNull(fnRight);
			return (Either<L2,R2>)Objects.requireNonNull(fnRight.apply(value));
		}
		@Override
		public @NotNull <R2> Either<L,R2> map(@NotNull final Function<? super R,? extends R2> rightMapper) {
			Objects.requireNonNull(rightMapper);
			return Either.ofRight(rightMapper.apply(value));
		}
		@Override
		public @NotNull <L2> Either<L2, R> mapLeft(@NotNull final Function<? super L,? extends L2> leftMapper) {
			Objects.requireNonNull(leftMapper);
			return _coerce();
		}
		////////// Match
		@Override
		public @NotNull Either<L,R> match(@NotNull final Consumer<? super R> rightConsumer) {
			Objects.requireNonNull( rightConsumer );
			rightConsumer.accept(value);
			return this;
		}
		@Override
		public @NotNull Either<L,R> matchLeft(@NotNull final Consumer<? super L> leftConsumer) {
			Objects.requireNonNull(leftConsumer);
			// do nothing
			return this;
		}
		@Override
		public @NotNull Either<L,R> biMatch(@NotNull final Consumer<? super L> leftConsumer,
											@NotNull final Consumer<? super R> rightConsumer) {
			Objects.requireNonNull(leftConsumer);
			Objects.requireNonNull(rightConsumer);
			rightConsumer.accept(value);
			return this;
		}
		////////// Util
		/**
		 * Coerce the empty Left parameter to the new type.
		 * Does not create a new object.
		 *
		 * @param <L2> new Left parameter
		 * @return coerced Right
		 */
		@NotNull @SuppressWarnings("unchecked")
		private <L2> Either<L2,R> _coerce() {
			return (Either<L2,R>)this;
		}
	}
}
