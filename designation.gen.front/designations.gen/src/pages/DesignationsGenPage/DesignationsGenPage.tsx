import SearchBoxAutoComponent from "../../components/SearchBocAutoComponent/SearchBoxAutoComponent";

import './style.css';

function DesignationsGenPage() {
  return (
    <body>
      <div className="container">
          <header>
              <h1>Gerador de Designação</h1>
          </header>
        <SearchBoxAutoComponent />
      </div>
    </body>
  );
};

export default DesignationsGenPage;