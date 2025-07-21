import CNLsTable from "../../components/CNLsTableComponent/CNLsTableComponent";
import OutputCNLBox from "../../components/OutputBoxComponent/OutputMunicipalitiesBoxComponent";
import SearchBoxAutoComponent from "../../components/SearchBocAutoComponent/SearchBoxAutoComponent";
import KeyboardReturnIcon from '@mui/icons-material/KeyboardReturn';
import { useNavigate } from "react-router-dom";
import type { Municipalities } from "../../data/MunicipalitiesData";
import { useState } from "react";

import './CNLQueryPageStyle.css';

function CNLQueryPage() {
    const [selectedMunicipality, setSelectedMunicipality] = useState<Municipalities | null>(null);
    const navigate = useNavigate();

    return (
        <>
        <button className="return-menu" 
                onClick={() => {navigate('/')}}>
            <KeyboardReturnIcon/>
        </button>
        <div className="section-search-container">
            <h2>Pesquise por município</h2>
            <SearchBoxAutoComponent
                label="Cidade:"
                placeholder="Digite o nome da cidade"
                value={selectedMunicipality}
                onChange={setSelectedMunicipality} />
            <OutputCNLBox
                municipality={selectedMunicipality}
                label="CNL:"
                placeholder="CNL selecionado" />
        </div>
        <CNLsTable />
        </>
    );
}

export default CNLQueryPage;