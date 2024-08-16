package com.gmmapowell.script.kNodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.core.JsonGenerator;

public class KNode<T extends KNodeItem> {
	private final T item;
	private final int x, y, z;
	private float xpos, ypos, zpos;
	private final List<Link<T>> links = new ArrayList<>();

	public KNode(T item, int which, int ncells) {
		this.item = item;
		this.x = which % ncells;
		this.y = (which/ncells) % ncells;
		this.z = which/ncells/ncells;
	}
	
	public String name() {
		// TODO Auto-generated method stub
		return item.name();
	}

	public void locate(int ncells, KNode<T>[] occupation, Random r) {
		float cx = (x-ncells/2f + 0.5f)*Galaxy.CellSize;
		float cy = (y-ncells/2f + 0.5f)*Galaxy.CellSize;
		float cz = (z-ncells/2f + 0.5f)*Galaxy.CellSize;
		this.xpos = cx + (r.nextFloat()-0.5f)*Galaxy.CellSize;
		this.ypos = cy + (r.nextFloat()-0.5f)*Galaxy.CellSize;
		this.zpos = cz + (r.nextFloat()-0.5f)*Galaxy.CellSize;
//		System.out.println("X: " + x + " => " + cx + " => " + xpos);
//		System.out.println("Y: " + y + " => " + cy + " => " + ypos);
//		System.out.println("Z: " + z + " => " + cz + " => " + zpos);
		for (int nx=-1;nx<=0;nx++) {
			if (this.x == 0 && nx == -1) continue;
			for (int ny=-1;ny<=0;ny++) {
				if (this.y == 0 && ny == -1) continue;
				for (int nz=-1;nz<=0;nz++) {
					if (this.z == 0 && nz == -1) continue;
					if (nx+ny+nz == 0) continue;
					int other = (x+nx) + ncells*(y+ny) + ncells*ncells*(z+nz);
//					System.out.println("Nudge " + x + ","+y+"," + z + " compared to " + nx + ", " + ny + ", "+nz + " = " + other);
					nudge(occupation[other], nx, ny, nz);
				}
			}
		}
	}

	private void nudge(KNode<T> relative, int nx, int ny, int nz) {
		if (relative == null)
			return;
		
		float rx = xpos - relative.xpos;
		float ry = ypos - relative.ypos;
		float rz = zpos - relative.zpos;
		float dist = (float) Math.sqrt( rx*rx + ry*ry + rz*rz );
		if (dist < Galaxy.MinDist) {
//			System.out.println("nudge " + this + " relative to " + relative + ": dist = " + dist + " vs = " + nx + " " + ny + " " + nz);
			if (nx+ny+nz == -1) {
				// in 1d
				if (nx == -1) {
					this.xpos = figure1d(relative.xpos, ry*ry, rz*rz);
				} else if (ny == -1) {
					this.ypos = figure1d(relative.ypos, rz*rz, rx*rx);
				} else if (nz == -1) {
					this.zpos = figure1d(relative.zpos, rx*rx, ry*ry);
				}
			} else if (nx+ny+nz == -2) {
				// move 2
				if (nx == 0) {
					float[] m2 = figure2d(rx*rx, ypos, ry, zpos, rz);
					this.ypos = m2[0];
					this.zpos = m2[1];
				} else if (ny == 0) {
					float[] m2 = figure2d(ry*ry, zpos, rz, xpos, rx);
					this.zpos = m2[0];
					this.xpos = m2[1];
				} else if (nz == 0) {
					float[] m2 = figure2d(rz*rz, xpos, rx, ypos, ry);
					this.xpos = m2[0];
					this.ypos = m2[1];
				}
			} else {
				// move all 3
				float old3d = dist*dist;
				float new3d = Galaxy.MinDist*Galaxy.MinDist;
				float scale = (float) Math.sqrt(new3d/old3d);
				float extend = scale - 1;
				this.xpos += rx*extend;
				this.ypos += ry*extend;
				this.zpos += rz*extend;
			}
			
			// check our work
//			dist = (float) Math.sqrt( (xpos - relative.xpos)*(xpos - relative.xpos) + (ypos - relative.ypos)*(ypos - relative.ypos) + (zpos - relative.zpos)*(zpos - relative.zpos) );
//			System.out.println("nudged to " + this + "; dist = " + dist);
		}
	}

	private float figure1d(float r, float d1, float d2) {
		return r + (float) Math.sqrt(Galaxy.MinDist*Galaxy.MinDist - d1 - d2);
	}

	private float[] figure2d(float cd2, float m1, float r1, float m2, float r2) {
		float old2d = r1*r1+r2*r2;
		float new2d = Galaxy.MinDist*Galaxy.MinDist - cd2;
		float scale = (float) (Math.sqrt(new2d/old2d));
		float extend = scale-1;
		return new float[] { m1 + r1*extend, m2 + r2*extend };
	}

	// This should probably be bi-directional
	public void join(KNode<T> target) {
		for (Link<T> link : links) {
			if (link.from == target || link.to == target)
				return;
		}
		Link<T> l = new Link<T>(this, target);
		links.add(l);
		target.links.add(l);
	}
	
	public void asJson(JsonGenerator gen) throws IOException {
		gen.writeStartObject();
		gen.writeNumberField("x", xpos);
		gen.writeNumberField("y", ypos);
		gen.writeNumberField("z", zpos);
		gen.writeStringField("img", item.image());
		if (item.overlayImage() != null)
			gen.writeStringField("image", item.overlayImage());
		gen.writeFieldName("item");
		item.asJson(gen);
		gen.writeEndObject();
	}

	public void meta(JsonGenerator gen) throws IOException {
		gen.writeFieldName(item.name());
		gen.writeStartObject();
		gen.writeNumberField("x", xpos);
		gen.writeNumberField("y", ypos);
		gen.writeNumberField("z", zpos);
		gen.writeEndObject();
	}

	public void tunnels(JsonGenerator gen) throws IOException {
		if (!links.isEmpty()) {
			for (Link<T> l : links) {
				if (l.to != this) {
					gen.writeStartObject();
					gen.writeStringField("from", name());
					gen.writeStringField("to", l.to.name());
					gen.writeFieldName("color");
					gen.writeArray(new double[] { 1, 1, 1, 0.5 }, 0, 4);
					gen.writeEndObject();
				}
			}
		}
	}

	@Override
	public String toString() {
		return "KN["+xpos+","+ypos+","+zpos+"]";
	}
}
