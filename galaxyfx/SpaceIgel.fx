package galaxyfx;

import galaxyfx.program.*;
import galaxyfx.program.action.*;
import galaxyfx.program.TuningOptions;
import galaxyfx.spacecraft.system.*;

Program{
    name: "SpaceIgel"

    autoHarvest: false; //disable backward compatibility mode

    options: TuningOptions {
        cargo: 2,
        speed: 2,
        teamwork: 0,
        radar: 0
    }
    var path: ResourceItem[];
    var lastCargo: Float = -1;
    var term:   CargoTerminalItem;

    function nearestTerminal(): CargoTerminalItem {
        var min = Number.MAX_VALUE;
        var res: CargoTerminalItem;
        for(t in map.terminals) {
            var cur = distanceTime(t);
            if (cur < min) {
                min = cur;
                res = t;
            }
        }
        return res;
    }


    function  computePath(): ResourceItem[] {
        var maxCost = Number.MIN_VALUE;
        var res: ResourceItem = null;
        var d0: Float;
        var d1: Float;
        for(r in map.resources) {
            if ( not map.contains(r) )
                continue;
//            d0 = Point.distance(position, r);
//            d1 = Point.distance(r, term);
            var curCost = r.value / distanceTime([r, term]);
            if (curCost > maxCost) {
                maxCost = curCost;
                res = r;
            }
        }
        if ( res != null ) {
          d0 = Point.distance(position, res);
          d1 = Point.distance(res, term);
          maxCost = Number.MIN_VALUE;
          var res2: ResourceItem = null;
          for(r in map.resources) {
            if ( not map.contains(r) or r.equals(res) )
                continue;
            var dd: Float;
            var curCost: Float = 0;
            if ( d0 > d1 )
                dd = Point.distance(position, r) + Point.distance(r, res)
            else
                dd = Point.distance(res, r) + Point.distance(r, term);
            curCost = r.value / dd;
            if (curCost > maxCost) {
                maxCost = curCost;
                res2 = r;
            }
          }
          print("res:");println(res);
          print("res2:");println(res2);
          if (res2 != null) {
              if ( d0 > d1 )
                return [res2, res]
              else
                return [res, res2];
          } else
            return [res];
        } else
            return null;
    }


    public override function setup():Void{
    }

    public override function nextStep():Action{
        if (action == null) {
            term = nearestTerminal();
            if (cargoCapacity == cargo) {
                lastCargo = -1;
                return CompoundAction {
                    actions: [
                        MoveAction {
                            path: [term]
                        },
                        UnloadAction {
                            limit: cargo
                    }

                    ]
                }
            } else {
                if (sizeof path == 0)
                    path = computePath();
                if (sizeof path > 0) {
                    var r: ResourceItem = path[0];
                    delete r from path;
                    var acts: Action[] = null;
                    print("mv:");println(r);
                    insert MoveAction {path: [r]} into acts;
                    insert HarvestAction {limit: r.value} into acts;
                    if ( cargo <= lastCargo ) {
                        insert SpaceScanAction {} into acts;
                        lastCargo = -1;
                        path = null;
                    } else
                        lastCargo = cargo;
                    var a = CompoundAction { actions: [acts] }
                    return a
                } else
                //no resources on map
                if (cargo > 0) {
                    return CompoundAction {
                        actions: [
                            MoveAction {
                                path: [term]
                            },
                            UnloadAction {
                                limit: cargo
                        }]
                    }
                }
            }
        }
        null;
    }
        
}
