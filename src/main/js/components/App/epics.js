import { Observable } from 'rxjs';

import {
  RUN_TRADE_STREAM,
  STOP_TRADE_STREAM,
  OFFER_TRADE,
} from './constants';

import { saveChartData, saveTradesData } from './actions';

let isRestEnabled = false;

const socket$ = Observable
  .webSocket({
    url: 'ws://localhost:8080/stream',
  });
const restGet$ = timestamp => Observable.fromPromise(fetch(`http://localhost:8080/v1/trades${timestamp ? `?timestamp=${timestamp}` : ''}`));
const restPost$ = trade => Observable.fromPromise(fetch('http://localhost:8080/v1/trades', {
  body: JSON.stringify(trade), // must match 'Content-Type' header
  method: 'POST',
  headers: new Headers({
    'Content-Type': 'application/json',
  }),
}));

const websocketTradesEpic = action$ => Observable.merge(
  action$.ofType(RUN_TRADE_STREAM)
    .mergeMap(() => restGet$(Date.now())
      .mergeMap((response) => {
        if (response.ok) {
          isRestEnabled = true;
          return Observable.interval(3000)
            .scan(() => ({
              current: Date.now(), previous: Date.now() - 3000
            }), a => ({
              current: Date.now(), previous: a.current
            }))
            .map(t2 => t2.previous)
            .concatMap(restGet$)
            .mergeMap(r => Observable.fromPromise(r.json()))
            .mergeMap(arr => Observable.from(arr))
            .retryWhen(e => e.zip(Observable.interval(60000)));
        }
        return socket$.retryWhen(e => e.zip(Observable.interval(1000)));
      })
      .windowCount(15)
      .mergeAll()
      .map((payload) => {
        switch (payload.type) {
          case 'TRADE':
            return saveTradesData(payload);
          case 'PRICE':
          case 'AVG_PRICE':
            return saveChartData(payload);
          default:
            return payload;
        }
      })
      .takeUntil(action$.ofType(STOP_TRADE_STREAM))
      .catch(() => Observable.of({ type: 'ERROR' }))), // eslint-disable-line object-curly-newline
  action$.ofType(OFFER_TRADE)
    .mergeMap((msg) => {
      if (isRestEnabled) {
        return restPost$(msg.payload)
          .mergeMap(() => Observable.empty());
      }

      socket$.socket.send(JSON.stringify(msg.payload));
      return Observable.empty();
    })
);

export default websocketTradesEpic;
