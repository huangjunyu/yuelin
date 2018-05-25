package com.yuelin.o2cabin;



import com.yuelin.AZItemEntity;

import java.util.Comparator;

public class LettersComparator implements Comparator<Disease> {

	public int compare(Disease o1, Disease o2) {
		if (o1.getSortLetter().equals("@")
			|| o2.getSortLetter().equals("#")) {
			return 1;
		} else if (o1.getSortLetter().equals("#")
				   || o2.getSortLetter().equals("@")) {
			return -1;
		} else {
			return o1.getSortLetter().compareTo(o2.getSortLetter());
		}
	}

}
