package com.lily56.spiders2.common.entity.movement;

import java.util.EnumSet;

import javax.annotation.Nullable;

import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Direction;

public class DirectionalPathPoint extends PathPoint {
	protected static final long ALL_DIRECTIONS = AdvancedWalkNodeProcessor.packDirection(Direction.UP, AdvancedWalkNodeProcessor.packDirection(Direction.DOWN, AdvancedWalkNodeProcessor.packDirection(Direction.NORTH, AdvancedWalkNodeProcessor.packDirection(Direction.EAST, AdvancedWalkNodeProcessor.packDirection(Direction.SOUTH, AdvancedWalkNodeProcessor.packDirection(Direction.WEST, 0L))))));

	protected static final Direction[] DIRECTIONS = Direction.values();

	private final Direction[] pathableSides;
	private final Direction pathSide;

	private final boolean isDrop;
	
	public DirectionalPathPoint(int x, int y, int z, long packed, boolean isDrop) {
		super(x, y, z);

		EnumSet<Direction> directionsSet = EnumSet.noneOf(Direction.class);
		for(int i = 0; i < DIRECTIONS.length; i++) {
			Direction dir = DIRECTIONS[i];

			if(AdvancedWalkNodeProcessor.unpackDirection(dir, packed)) {
				directionsSet.add(dir);
			}
		}

		this.pathableSides = directionsSet.toArray(new Direction[0]);
		this.pathSide = null;
		
		this.isDrop = isDrop;
	}

	public DirectionalPathPoint(PathPoint point, long packed, boolean isDrop) {
		this(point.x, point.y, point.z, packed, isDrop);

		this.index = point.index;
		this.totalPathDistance = point.totalPathDistance;
		this.distanceToNext = point.distanceToNext;
		this.distanceToTarget = point.distanceToTarget;
		this.previous = point.previous;
		this.visited = point.visited;
		this.field_222861_j = point.field_222861_j;
		this.costMalus = point.costMalus;
		this.nodeType = point.nodeType;
	}

	public DirectionalPathPoint(PathPoint point) {
		this(point, ALL_DIRECTIONS, false);
	}

	private DirectionalPathPoint(int x, int y, int z, Direction[] pathableSides, Direction pathSide, boolean isDrop) {
		super(x, y, z);

		this.pathableSides = new Direction[pathableSides.length];
		System.arraycopy(pathableSides, 0, this.pathableSides, 0, pathableSides.length);

		this.pathSide = pathSide;
		
		this.isDrop = isDrop;
	}

	public DirectionalPathPoint(PathPoint point, Direction pathSide) {
		super(point.x, point.y, point.z);

		this.index = point.index;
		this.totalPathDistance = point.totalPathDistance;
		this.distanceToNext = point.distanceToNext;
		this.distanceToTarget = point.distanceToTarget;
		this.previous = point.previous;
		this.visited = point.visited;
		this.field_222861_j = point.field_222861_j;
		this.costMalus = point.costMalus;
		this.nodeType = point.nodeType;

		if(point instanceof DirectionalPathPoint) {
			DirectionalPathPoint other = (DirectionalPathPoint) point;

			this.pathableSides = new Direction[other.pathableSides.length];
			System.arraycopy(other.pathableSides, 0, this.pathableSides, 0, other.pathableSides.length);
			
			this.isDrop = other.isDrop;
		} else {
			this.pathableSides = Direction.values();
		
			this.isDrop = false;
		}

		this.pathSide = pathSide;
	}
	
	public DirectionalPathPoint assignPathSide(Direction pathDirection) {
		return new DirectionalPathPoint(this, pathDirection);
	}

	@Override
	public PathPoint cloneMove(int x, int y, int z) {
		PathPoint pathPoint = new DirectionalPathPoint(x, y, z, this.pathableSides, this.pathSide, this.isDrop);
		pathPoint.index = this.index;
		pathPoint.totalPathDistance = this.totalPathDistance;
		pathPoint.distanceToNext = this.distanceToNext;
		pathPoint.distanceToTarget = this.distanceToTarget;
		pathPoint.previous = this.previous;
		pathPoint.visited = this.visited;
		pathPoint.field_222861_j = this.field_222861_j;
		pathPoint.costMalus = this.costMalus;
		pathPoint.nodeType = this.nodeType;
		return pathPoint;
	}

	/**
	 * Returns all pathable sides of this node, i.e. all sides the entity could potentially walk on
	 * @return
	 */
	public Direction[] getPathableSides() {
		return this.pathableSides;
	}

	/**
	 * Returns the side assigned to this node by the path this node is part of, or null if this node has not been assigned to a path
	 * @return
	 */
	@Nullable
	public Direction getPathSide() {
		return this.pathSide;
	}
	
	/**
	 * Returns whether this node represents a drop
	 * @return
	 */
	public boolean isDrop() {
		return this.isDrop;
	}
}
