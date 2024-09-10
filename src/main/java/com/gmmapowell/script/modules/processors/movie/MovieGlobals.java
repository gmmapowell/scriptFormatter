package com.gmmapowell.script.modules.processors.movie;

import java.io.IOException;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.script.processor.movie.DramatisPersonae;

public class MovieGlobals {
	private String title;
	private DramatisPersonae dp;

	public void configure(Region root, String dramatis, String title) {
		this.title = title;
		try {
			Place dpPlace = root.place(dramatis);
			dp = new DramatisPersonae(dpPlace);
		} catch (IOException e) {
			System.out.println("Error reading dramatis file: " + e.getMessage());
			return;
		}
	}

	public DramatisPersonae dramatis() {
		return dp;
	}

	public String extractTitle() {
		String ret = title;
		this.title = null;
		return ret;
	}
}
