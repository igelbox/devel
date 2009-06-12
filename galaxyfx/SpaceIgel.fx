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
        cargo: 2,
        speed: 2,
        teamwork: 0,
        radar: 0
    }
    var path: ResourceItem[];
    var term: CargoTerminalItem;
    var maxClamp: Float;
    var cargoK: Float;

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
        var toterm = cargoK > 0.66;
        var timeLimit = map.gameTime - time;
        var maxCost = Number.MIN_VALUE;
        var res: ResourceItem = null;
        for(r in map.resources) {
            if ( r.value < 0.1 )
                continue;
            var time;
            if (toterm) time = distanceTime([r, term])
            else time = distanceTime([r]);
            var curCost = Math.min(r.value, maxClamp) / time;
            if ( time > timeLimit )
                continue;
            if (curCost > maxCost) {
                maxCost = curCost;
                res = r;
            }
        }
        if ( res != null ) {
          var ff = (not toterm) or (Point.distance(position, res) > Point.distance(res, term));
          var res2: ResourceItem = null;
          for(r in map.resources) {
            if ( (r.value < 0.1) or r.equals(res) )
                continue;
            var time: Float;
            var curCost: Float = 0;
            var pth: Point[] = null;
            if (toterm) {
                if ( ff and not toterm ) pth = [r, res]
                else pth = [res, r];
                insert term into pth;
            } else
              pth = [res, r];
            time = distanceTime(pth);
            if ( time > timeLimit )
                continue;
            curCost = Math.min(r.value+res.value, maxClamp) / (time-0.2);
            if (curCost > maxCost) {
                maxCost = curCost;
                res2 = r;
            }
          }
          if (res2 != null) {
              var d0;
              var d1;
              if ( toterm ) {
                  d0 = distanceTime([res2, res, term]);
                  d1 = distanceTime([res, res2, term]);
              } else {
                  d0 = distanceTime([res2, res]);
                  d1 = distanceTime([res, res2]);
              }
              if ( d0 < d1 )
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
            maxClamp = cargoCapacity - cargo;
            cargoK = cargo / cargoCapacity;
            println(cargoK/(distanceTime(term)+0.1));
            if ((maxClamp == 0) or (cargoK/(distanceTime(term)+0.1) > 0.05)) {
                return CompoundAction {
                    actions: [ MoveAction {path: [term]}, UnloadAction {limit: cargo}]
                }
            } else {
//                if (sizeof path == 0)
                path = computePath();
                if (sizeof path > 0) {
                    var acts: Action[] = null;
                    for (r in path) {
                        insert MoveAction {path: [r]} into acts;
                        insert HarvestAction {limit: r.value} into acts;
                    }
                    return CompoundAction { actions: [acts] }
                } else
                //no resources on map
                if (cargo > 0) {
                    return CompoundAction {
                        actions: [ MoveAction {path: [term]}, UnloadAction {limit: cargo}]
                    }
                }
            }
        } else {
            if ( action instanceof CompoundAction ) {
                var ca = action as CompoundAction;
                var act = ca.actions[ca.currentIndex];
                if ( act instanceof MoveAction ) {
                    var ma: MoveAction = act as MoveAction;
                    var res: ResourceItem = null;
                    var p: Point = ma.path[0];
                    for (r in path)
                      if (r.equals(p)) {
                          res = r;
                          break;
                      }
                    if ( (res != null) and (Point.distance(res, position) < radar.range) ) {
                        for (r in radar.resources)
                            if (r.equals(res))
                                return null;
                        return SpaceScanAction{};
                    }
                }
                return null;
            }
        }
        return null;
    }
        
}
