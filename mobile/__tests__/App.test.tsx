/**
 * @format
 */

import React from 'react';
import ReactTestRenderer from 'react-test-renderer';
import App from '../App';

jest.mock('@react-native-async-storage/async-storage', () =>
  require('@react-native-async-storage/async-storage/jest/async-storage-mock')
);

test('renders correctly', async () => {
  await ReactTestRenderer.act(() => {
    ReactTestRenderer.create(<App />);
  });
});

test('renders navigation container', async () => {
  const tree = await ReactTestRenderer.act(() => {
    return ReactTestRenderer.create(<App />);
  });

  expect(tree).toBeTruthy();
});
