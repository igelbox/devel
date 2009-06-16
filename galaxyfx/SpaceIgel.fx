package galaxyfx;

import galaxyfx.program.*;
import galaxyfx.program.action.*;
import galaxyfx.program.TuningOptions;
import galaxyfx.spacecraft.system.*;
import java.lang.Math;

Program{
    name: "SpaceIgel"

    autoHarvest: false; //disable backward compatibility mode

    options: TuningOptions {
        cargo: 0,
        speed: 2,
        teamwork: 2,
        radar: 0
    }

    function inArray( obj: Object, arr: Object[] ): Boolean {
        for ( o in arr )
            if ( o == obj )
              return true;
        return false;
    }

    function nearestTerminal( pos: Point ): CargoTerminalItem {
        if ( sizeof map.terminals == 1 )
            return map.terminals[0];
        var min = Number.MAX_VALUE;
        var res: CargoTerminalItem;
        for( t in map.terminals ) {
            var cur;
            if ( pos == null )
                cur = distanceTime( t )
            else
                cur = distanceTime( pos, [t] );
            if ( cur < min ) {
                min = cur;
                res = t;
            }
        }
        return res;
    }

    function computePath(): ResourceItem[] {
        var p0: Point = position.sub(Point.rotate(Point{x:50, y:0}, direction));
        var p00: Point = p0;
        var p1: Point = position;
        var cargoLeft = cargoCapacity - cargo;
        var result: ResourceItem[] = [];
        while ( cargoLeft > 0 ) {
            var max: Float = Float.MIN_VALUE;
            var res: ResourceItem = null;
            var ntk: Float = cargoLeft / cargoCapacity;
            for ( r in map.resources ) {
                if ( (r.value < 0.1) or inArray(r, result) )
                    continue;
                var val = Math.min( r.value, cargoLeft );
                var cur: Float = val / distanceTime(p0, [p1, r]);
                var curT: Float = val / distanceTime(p0, [p1, r, nearestTerminal(r)]);
                cur = ntk*cur + (1-ntk)*curT;
                if ( cur > max ) {
                    max = cur;
                    res = r;
                }
            }
            if ( res == null ) {
//                println("res is null");
                break;
            }
            while ( res != null ) {
                var old_res: ResourceItem = res;
                var term = nearestTerminal( old_res );
                res = null;
                var v0: Point = res.sub( p1 );
                var len: Float = v0.length();
                for ( r in map.resources ) {
                    if ( (r.value < 0.1) or (r == old_res) or inArray(r, result) )
                        continue;
                    var v1: Point = r.sub( p1 );
                    var proj: Float = Point.dotProduct( v0, v1 )/len;
                    if ( (proj < 0.0) or (proj < len) or (proj/v1.length() < 0.85) )
                        continue;
                    var val = Math.min( r.value+old_res.value, cargoLeft );
                    var cur: Float = val / (distanceTime(p0, [p1, r, old_res]));
                    var curT: Float = val / distanceTime(p0, [p1, r, old_res, term]);
                    cur = ntk*cur + (1-ntk)*curT;
                    if ( cur > max ) {
                        max = cur;
                        res = r;
                    }
                }
                if ( res == null ) {
                    cargoLeft -= old_res.value;
//                    print("put:");println(cargoLeft);
                    insert old_res into result;
                    if ( sizeof result > 2 ) {
                        var _0: ResourceItem = result[sizeof result-3];
                        var _1: ResourceItem = result[sizeof result-2];
                        var _2: ResourceItem = result[sizeof result-1];
                        if ( distanceTime(p00, [_1, _0, _2]) < distanceTime(p00, [_0, _1, _2]) ) {
                            result[sizeof result-3] = _1;
                            result[sizeof result-2] = _0;
                            p0 = _1;
                            p1 = _0;
                        }
                    }
                    p00 = p0;
                    p0 = p1;
                    p1 = old_res;
                    break;
                }
            }
        }
        return result;
    }


    public override function setup(): Void {
    }

    public override function nextStep(): Action {
//        print("pos:");print(position);print(" dir:");print(direction);print(" back:");println(p0);
        if ( action == null ) {
            var term = nearestTerminal( null );
            var leftCK = 1-cargo/cargoCapacity;
            if ( (leftCK < 0.1) and (leftCK*distanceTime(term) < 0.5) )
                return CompoundAction { actions: [ MoveAction {path: [term]}, UnloadAction {}] };
            var path = computePath();
            if ( sizeof path > 0 ) {
                var acts: Action[] = null;
                for ( r in path ) {
                    insert MoveAction {path: [r]} into acts;
                    insert HarvestAction {limit:r.value} into acts;
                }
                return CompoundAction { actions: acts };
            }
            return CompoundAction { actions: [ MoveAction {path: [term]}, UnloadAction {}] };
        } else {
            if ( action instanceof CompoundAction ) {
                var ca = action as CompoundAction;
                var act = ca.actions[ca.currentIndex];
                if ( not((act instanceof MoveAction) and (ca.actions[ca.currentIndex+1] instanceof UnloadAction)) ) {
                    var timeLimit = map.gameTime - time - 10;
                    var term = nearestTerminal( null );
                    if ( timeLimit < distanceTime(term) )
                        return CompoundAction { actions: [ MoveAction {path: [term]}, UnloadAction {}] };
                }
                if ( (act instanceof MoveAction) and (ca.actions[ca.currentIndex+1] instanceof HarvestAction) ) {
                    var ma: MoveAction = act as MoveAction;
                    var p: Point = ma.path[ma.pathIndex];
                    var term = nearestTerminal( null );
                    var res: ResourceItem = (p as ResourceItem);
                    var rv = Math.min(res.value, cargoCapacity);
                    var cargoLeft = cargoCapacity-cargo;
                    if ( rv > cargoLeft ) {
                        var s0 = Math.min(rv, cargoLeft) / (distanceTime([p, term]) + 0.1);
                        var s1 = rv / (distanceTime([term, p]) + 1);
                        if ( s1 > s0 )
                            return CompoundAction { actions: [ MoveAction {path: [term]}, UnloadAction {}] };
                    }
                    if ( (time-map.timestamp) < 50 )
                        return null;
                    if ( Point.distance(p, position) > radar.range )
                        return null;
                    for ( r in radar.resources )
                        if ( r.equals(p) )
                            return null;
                    return SpaceScanAction{};
                }
            }
        }
        return null;
    }

}
