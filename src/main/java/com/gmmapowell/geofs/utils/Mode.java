package com.gmmapowell.geofs.utils;

import com.gmmapowell.geofs.Region;

public enum Mode {
	ENSURE {
		public Region sub(Region region, String s) {
			return region.ensureSubregion(s);
		}
	}, EXISTS {
		public Region sub(Region region, String s) {
			return region.subregion(s);
		}
	}, NEW {
		public Region sub(Region region, String s) {
			return region.newSubregion(s);
		}		
	};

	abstract Region sub(Region region, String s);
}
