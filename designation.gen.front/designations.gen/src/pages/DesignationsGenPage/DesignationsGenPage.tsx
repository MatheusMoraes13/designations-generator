import OutputBox from "../../components/OutputBoxComponent/OutputBoxComponent";
import SearchBoxAutoComponent from "../../components/SearchBocAutoComponent/SearchBoxAutoComponent";
import InputBox from "../../components/InputComponent/InputComponent";
import { useState } from "react";
import type { Municipalities } from "../../data/MunicipalitiesData";

import './style.css';

function DesignationsGenPage() {

  const [selectedMunicipality, setSelectedMunicipality] = useState<Municipalities | null>(null);

  return (
    <div className="container">
        <header>
            <h2>Gerador de Designação</h2>
        </header>
      <div className= "search-cnl-container">
        <h3>Selecione o município</h3>
        <SearchBoxAutoComponent
          value={selectedMunicipality}
          onChange={setSelectedMunicipality}
        />
        <OutputBox municipality={selectedMunicipality} />
      </div>
      <InputBox />
    </div>
  );
};

export default DesignationsGenPage;