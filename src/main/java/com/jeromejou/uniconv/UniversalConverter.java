package com.jeromejou.uniconv;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.commons.lang.StringUtils;


public class UniversalConverter
{
	private static final String FIELD_DIVIDER = ".";

	public static LinkedHashMap convert(Object source, LinkedHashMap mapping)
	{
		LinkedHashMap result = new LinkedHashMap();
		Class sourceClass = source.getClass();
		PatriciaTrie patriciaTrie = new PatriciaTrie();
		TreeMap treeMap = new TreeMap<>();

		mapping.forEach((key, value) -> {
			populateSinglePropertiesAndBuildTrie(source, result, sourceClass, treeMap, key, value);
		});

		patriciaTrie.putAll(treeMap);

		while (!patriciaTrie.isEmpty())
		{
			String lastKey = patriciaTrie.lastKey().toString();

			// Set target object and target class and build structure
			Object targetObject = getTargetObject(lastKey, source);
			Class targetClass = targetObject.getClass();

			PatriciaTrie tempTrie = new PatriciaTrie();
			int hierarchyDepth = StringUtils.countMatches(lastKey, FIELD_DIVIDER);
			patriciaTrie.keySet().forEach(key -> {
				if (hierarchyDepth == StringUtils.countMatches(key.toString(), FIELD_DIVIDER)) {
					tempTrie.put(key, patriciaTrie.get(key));
				}
			});

			populateValuesForGroup(patriciaTrie, tempTrie, lastKey, targetObject, result);
		}

		return result;

	}

	protected static void populateValuesForGroup(final PatriciaTrie patriciaTrie, final PatriciaTrie tempTrie, final String lastKey, final Object targetObject,
			final LinkedHashMap result)
	{
		String prefix = lastKey.substring(0, lastKey.lastIndexOf(FIELD_DIVIDER));
		SortedMap samePrefixGroup = tempTrie.prefixMap(prefix);
		List toErase = new ArrayList();

		samePrefixGroup.keySet().forEach(key -> {
			LinkedHashMap mapToPopulate = populateResultWithComplexStructure(tempTrie.get(key).toString(), result);
			String originPropertyName = key.toString().substring(key.toString().lastIndexOf(FIELD_DIVIDER) + 1);
			String targetPropertyName = samePrefixGroup.get(key).toString()
					.substring(samePrefixGroup.get(key).toString().lastIndexOf(FIELD_DIVIDER) + 1);

			try
			{
				mapToPopulate.put(targetPropertyName,
						targetObject.getClass().getMethod("get" + originPropertyName).invoke(targetObject).toString());
			}
			catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
			{
				// Ignores mapping which doesn't applies
			}

			toErase.add(key);
		});

		toErase.forEach(keyToErase -> patriciaTrie.remove(keyToErase));
	}

	protected static void populateSinglePropertiesAndBuildTrie(final Object source, final LinkedHashMap result,
			final Class sourceClass, final Map patriciaTrie, final Object key, final Object value)
	{
		if (key.toString().contains(FIELD_DIVIDER))
		{
			patriciaTrie.put(key.toString(), value.toString());
		}
		else
		{
			try
			{
				result.put(value.toString(), sourceClass.getMethod("get" + key.toString()).invoke(source).toString());
			}
			catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
			{
				// Ignores mapping which doesn't applies
			}
		}
	}

	protected static Object getTargetObject(final String originKey, Object source)
	{
		Object result = source;
		final String[] keySplit = StringUtils.split(originKey, FIELD_DIVIDER);
		Iterable<String> keySections = Arrays.asList(keySplit);
		final Iterator<String> iterator = keySections.iterator();

		for (String keySection : keySections)
		{
			iterator.next();

			if (iterator.hasNext())
			{
				try
				{
					result = result.getClass().getMethod("get" + keySection.toString()).invoke(result);
				}
				catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
				{
					// Ignores mapping which doesn't applies
				}

			}
		}
		return result;
	}

	protected static LinkedHashMap populateResultWithComplexStructure(final String targetKey, final LinkedHashMap result)
	{
		final String[] targetSplit = StringUtils.split(targetKey, FIELD_DIVIDER);
		Iterable<String> targetPortions = Arrays.asList(targetSplit);
		final Iterator<String> targetIterator = targetPortions.iterator();
		LinkedHashMap intermediateLinkedMap = result;

		for (String targetPortion : targetPortions)
		{
			targetIterator.next();

			if (targetIterator.hasNext())
			{
				if (intermediateLinkedMap.get(targetPortion) == null)
				{
					intermediateLinkedMap.put(targetPortion, new LinkedHashMap<>());
				}
				intermediateLinkedMap = (LinkedHashMap) intermediateLinkedMap.get(targetPortion);
			}
		}

		return intermediateLinkedMap;
	}

}
