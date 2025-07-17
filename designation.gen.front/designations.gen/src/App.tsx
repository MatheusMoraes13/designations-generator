import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

import DesignationsGenPage from './pages/DesignationsGenPage/DesignationsGenPage';

export function App() {
  return (
    <Router>
      <Routes>
        <Route path="/DesignationsGenPage" element={<DesignationsGenPage />} />
      </Routes>
    </Router>
  );
}