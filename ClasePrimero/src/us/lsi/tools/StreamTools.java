package us.lsi.tools;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Spliterator;
import java.util.TreeMap;
import java.util.Spliterators.AbstractSpliterator;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class StreamTools {
	
	public static <L, R, T> Spliterator<T> zip(Spliterator<L> lefts, Spliterator<R> rights,
			BiFunction<L, R, T> combiner) {
		return new AbstractSpliterator<T>(
				Long.min(lefts.estimateSize(), rights.estimateSize()),
				lefts.characteristics() & rights.characteristics()) {
			@Override
			public boolean tryAdvance(Consumer<? super T> action) {
				return lefts.tryAdvance(left -> rights.tryAdvance(right -> action.accept(combiner.apply(left, right))));
			}
		};
	}
	
	public static <L, R, T> Stream<T> zip(Stream<L> leftStream, Stream<R> rightStream, BiFunction<L, R, T> combiner) {
		Spliterator<L> lefts = leftStream.spliterator();
		Spliterator<R> rights = rightStream.spliterator();
		return StreamSupport.stream(zip(lefts,rights,combiner), leftStream.isParallel() || rightStream.isParallel());
	}
	
	public static <E> Stream<Enumerate<E>> enumerate(Stream<E> stream, Integer start){
		Stream<Integer> st = Stream.iterate(start,e->e+1);
		return zip(stream,st,(e,n)->Enumerate.of(n, e));
	}
	
	public static <E> Stream<Enumerate<E>> enumerate(Stream<E> stream){
		return enumerate(stream,0);
	}
	
	public static <E> Iterable<E> toIterable(Stream<E> st){
		return st::iterator;
	}
	
	public static <E> List<E> toList(Stream<E> st){
		return st.collect(Collectors.toList());
	}
	
	public static <E> Set<E> toSet(Stream<E> st){
		return st.collect(Collectors.toSet());
	}
	
	public static <E> SortedSet<E> toSortedSet(Stream<E> st){
		return st.collect(Collectors.toCollection(()->new TreeSet<>()));
	}
	
	public static <E> SortedSet<E> toSortedSet(Stream<E> st, Comparator<E> cmp){
		return st.collect(Collectors.toCollection(()->new TreeSet<>(cmp)));
	}
	
	public static <E,K> Map<K,E> toMap(Stream<E> st, Function<E,K> key){
		return st.collect(Collectors.toMap(key,x->x));
	}
	
	public static <E,K,V> Map<K,V> toMap(Stream<E> st, Function<E,K> key, Function<E,V> value){
		return st.collect(Collectors.toMap(key,value));
	}
	
	public static <E,K,V> Map<K,V> toMap(Stream<E> st, Function<E,K> key, Function<E,V> value, BinaryOperator<V> op){
		return st.collect(Collectors.toMap(key,value,op));
	}
	
	public static <E,K> SortedMap<K,List<E>> groupingListSorted(
			Stream<E> st, 
			Function<E,K> key,
			Comparator<K> cmp){
		return st.collect(Collectors.groupingBy(key,
				() -> new TreeMap<K,List<E>>(cmp),
				Collectors.toList()));
	}
	
	public static <E,K,V> SortedMap<K,List<V>> groupingListSorted(
			Stream<E> st, 
			Function<E,K> key,
			Comparator<K> cmp,
			Function<E,V> value){
		return st.collect(Collectors.groupingBy(key,
				() -> new TreeMap<K,List<V>>(cmp),
				Collectors.mapping(value,Collectors.toList())));
	}
	
	public static <E,K> SortedMap<K,Set<E>> groupingSetSorted(
			Stream<E> st, 
			Function<E,K> key,
			Comparator<K> cmp){
		return st.collect(Collectors.groupingBy(key,
				() -> new TreeMap<K,Set<E>>(cmp),
				Collectors.toSet()));
	}
	
	public static <E,K,V> SortedMap<K,Set<V>> groupingSetSorted(
			Stream<E> st, 
			Function<E,K> key,
			Comparator<K> cmp,
			Function<E,V> value){
		return st.collect(Collectors.groupingBy(key,
				() -> new TreeMap<K,Set<V>>(cmp),
				Collectors.mapping(value,Collectors.toSet())));
	}
	
	public static <E,K> SortedMap<K,E> groupingReduceSorted(
			Stream<E> st, 
			Function<E,K> key, 
			Comparator<K> cmp,
			BinaryOperator<E> op,
			E a0
			){
		return st.collect(Collectors.groupingBy(key,
				() -> new TreeMap<K,E>(cmp),
				Collectors.reducing(a0,op)));
	}
	
	public static <E,K,V> SortedMap<K,V> groupingReduceSorted(
			Stream<E> st, 
			Function<E,K> key, 
			Comparator<K> cmp,
			Function<E,V> value, 
			BinaryOperator<V> op,
			V a0){
		return st.collect(Collectors.groupingBy(key,
				() -> new TreeMap<K,V>(cmp),
				Collectors.reducing(a0,value,op)));
	}
	
	public static <K,E> Map<K,List<E>> groupingList(Stream<E> st,Function<E,K> key){
		return st.collect(Collectors.groupingBy(key));
	}
	
	public static <K,E,R> Map<K,R> groupingListAndThen(Stream<E> st,Function<E,K> key, Function<List<E>,R> f){
		return st.collect(Collectors.groupingBy(key,Collectors.collectingAndThen(Collectors.toList(),f)));
	}
	
	public static <E,K,T> Map<K,List<T>> groupingList(Stream<E> st,Function<E,K> key, Function<E,T> value){
		return st.collect(Collectors.groupingBy(key,Collectors.mapping(value,Collectors.toList())));
	}
	
	public static <E,K,T,R> Map<K,R> groupingListAndThen(Stream<E> st,Function<E,K> key, 
			Function<E,T> value, Function<List<T>,R> f){
		return st.collect(Collectors.groupingBy(key,
				Collectors.mapping(value,
						Collectors.collectingAndThen(Collectors.toList(),f))));
	}
	
	public static <E,K> Map<K,Set<E>> groupingSet(Stream<E> st,Function<E,K> key){
		return st.collect(Collectors.groupingBy(key,Collectors.toSet()));
	}
	
	public static <E,K,T> Map<K,Set<T>> groupingSet(Stream<E> st,Function<E,K> key, Function<E,T> value){
		return st.collect(Collectors.groupingBy(key,Collectors.mapping(value,Collectors.toSet())));
	}
	
	public static <E,K,T,R> Map<K,R> groupingSetAndThen(Stream<E> st,Function<E,K> key, 
			Function<E,T> value, Function<Set<T>,R> f){
		return st.collect(Collectors.groupingBy(key,Collectors.mapping(value,
				Collectors.collectingAndThen(Collectors.toSet(),f))));
	}
	
	public static <E,K> Map<K,SortedSet<E>> groupingSortedSet(Stream<E> st,Function<E,K> key){
		return st.collect(Collectors.groupingBy(key,Collectors.toCollection(TreeSet::new)));
	}
	
	public static <E,K> Map<K,Integer> counting(Stream<E> st,Function<E,K> key){
		return st.collect(Collectors.groupingBy(key,
				Collectors.collectingAndThen(Collectors.counting(),Long::intValue)));
	}
	
	public static <E> Map<E,Integer> counting(Stream<E> st){
		return st.collect(Collectors.groupingBy(x->x,
				Collectors.collectingAndThen(Collectors.counting(),Long::intValue)));
	}
	
	public static <E,K> Map<K,E> groupingReduce(Stream<E> st, Function<E,K> key, BinaryOperator<E> op){
		return st.collect(Collectors.groupingBy(key,
				Collectors.collectingAndThen(Collectors.reducing(op),e->e.get())));
	}
	
	public static <E,K> Map<K,E> groupingReduce(Stream<E> st, Function<E,K> key, BinaryOperator<E> op, E a0){
		return st.collect(Collectors.groupingBy(key,Collectors.reducing(a0,op)));
	}
	
	public static <E,K,T> Map<K,T> groupingReduce(Stream<E> st, Function<E,K> key, Function<E,T> map, BinaryOperator<T> op, T a0){
		return st.collect(Collectors.groupingBy(key,Collectors.reducing(a0,map,op)));
	}

}
