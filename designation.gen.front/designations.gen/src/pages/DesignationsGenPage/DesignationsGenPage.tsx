import SearchBoxAutoComponent from "../../components/SearchBocAutoComponent/SearchBoxAutoComponent";

import './style.css';

function DesignationsGenPage() {
  return (
    <div className="container">
        <header>
            <h2>Gerador de Designação</h2>
        </header>
      <SearchBoxAutoComponent />
    </div>
  );
};

export default DesignationsGenPage;