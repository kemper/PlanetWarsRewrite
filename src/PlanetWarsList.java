import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;


public abstract class PlanetWarsList<T, K extends PlanetWarsList<T,K>> {
	List<T> items;
	
	public PlanetWarsList(Collection<T> items) {
		this.items = new ArrayList<T>(items);
	}

	public PlanetWarsList(T...items) {
		this(Arrays.asList(items));
	}
	
	T first() {
		return items.get(0);
	}

	K remove(T planet) {
		List<T> newPlanets = new ArrayList<T>(items);
		newPlanets.remove(planet);
		return build(newPlanets);
	}

	public T second() {
		if (size() == 0) return null;
		return items.get(1);
	}

	public int size() {
		return items.size();
	}

	public T last() {
		if (size() == 0) return null;
		return items.get(items.size() - 1);
	}
	
	public T third() {
		if (size() == 0) return null;
		return items.get(2);
	}

	List<T> items() {
		return new ArrayList<T>(items);
	}

	public K unique() {
		return build(new ArrayList<T>(new LinkedHashSet<T>(this.items)));
	}

	public K minus(K list) {
		List<T> newItems = new ArrayList<T>(this.items);
		newItems.removeAll(list.items);
		return build(newItems);
	}
	
	public K minus(T...inNeedOfRescue) {
		return minus(build(Arrays.asList(inNeedOfRescue)));
	}

	abstract K build(List<T> items);
	abstract K build(T...items);

	void print() {
		System.out.println(toString());
	}

	public String toString() {
		StringBuilder string = new StringBuilder(getClass().toString() + ": ");
		for (T t : items) {
			string.append(t.toString());
			string.append("\n");
		}
		return string.toString();
	}

	public K before(T planet) {
		List<T> before = new ArrayList<T>();
		for (T other : items) {
			if (planet.equals(other)) {
				return build(before);
			}
			before.add(other);
		}
		return build(before);
	}

	public boolean contains(Planet planet) {
		return items.contains(planet);
	}

	public boolean empty() {
		return size() == 0;
	}

	public K slice(int start, int end) {
		List<T> closer = new ArrayList<T>();
		if (start < size()) {
			for(int i = start; i < end && i < size(); i++) {
				closer.add(items.get(i));
			}
		}
		return build(closer);
	}

	public K union(K k) {
		LinkedHashSet<T> set = new LinkedHashSet<T>(items);
		set.addAll((Collection<T>)k.items);
		return build(new ArrayList<T>(set));
	}
	
	public K plus(T...t) {
		return union(build(t));
	}
	
	public K plus(K k) {
		return union(k);
	}

	public K replace(K k) {
		return minus(k).union(k);
	}

	public K union(T...t) {
		return union(build(t));
	}

	public K reverse() {
		List<T> planets = items;
		Collections.reverse(planets);
		return build(planets);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		K other = (K) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}

}
