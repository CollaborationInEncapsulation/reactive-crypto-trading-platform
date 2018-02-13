/* eslint global-require: "error" */
import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { AppContainer } from 'react-hot-loader';
import { BrowserRouter as Router, Route } from 'react-router-dom';

import './index.scss';
import createStore from './store';
import App from './components/App';


const render = (store) => {
  ReactDOM.render(
    <AppContainer>
      <Provider store={store}>
        <Router>
          <Route exact path='/' component={App} />
        </Router>
      </Provider>
    </AppContainer>,
    document.getElementById('root')
  );
};

render(createStore());

if (module.hot) {
  module.hot.accept('./components/App', () => render(App));
}
