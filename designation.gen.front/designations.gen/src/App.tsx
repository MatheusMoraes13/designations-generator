import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

import DesignationsGenPage from './pages/DesignationsGenPage/DesignationsGenPage';
import GlobaLColors from './assets/GlobalColors';

export function App() {
  return (
    <><GlobaLColors /><Router>
      <Routes>
        <Route path="/DesignationsGenPage" element={<DesignationsGenPage />} />
      </Routes>
    </Router></>
  );
}