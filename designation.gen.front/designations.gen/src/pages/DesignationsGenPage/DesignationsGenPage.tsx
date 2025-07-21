import OutputCNLBox from "../../components/OutputBoxComponent/OutputMunicipalitiesBoxComponent";
import SearchBoxAutoComponent from "../../components/SearchBocAutoComponent/SearchBoxAutoComponent";
import InputBox from "../../components/InputComponent/InputComponent";
import SelectTextFields from "../../components/SelectCircuitTypeComponent/SelectCircuitTypeComponent";
import OutputDesignationBox from "../../components/OutputBoxComponent/OutPutDesignationBoxComponent";
import GenerateAndCopyDesignationButton from "../../components/GenerateAndCopyDesignationButtonComponent/GenerateAndCopyButtonComponent";
import { useState } from "react";
import type { Municipalities } from "../../data/MunicipalitiesData";
import type { DesignationResponse } from "../../data/DesignationsData";
import { BaseURL } from "../../functions/DesignationsAPIFunction";
import ActionAlert, { type ActionAlertProps } from "../../components/AlertsComponent/AlertsComponent";

import './style.css';

function DesignationsGenPage() {

  const [selectedMunicipality, setSelectedMunicipality] = useState<Municipalities | null>(null);
  const [generatedDesignation, setGeneratedDesignation] = useState<DesignationResponse | null>(null);
  const [alertInfo, setAlertInfo] = useState<Omit<ActionAlertProps, 'onClose'> | null>(null);
  const [contractId, setContractId] = useState('');
  const [circuitType, setCircuitType] = useState('IP'); 
  const [isLoading, setIsLoading] = useState(false);

  const handleCloseAlert = () => {
    setAlertInfo(null);
  };

  const handleGenerateClick = async () => {
    setAlertInfo(null);

    if (!selectedMunicipality || !contractId) {
      setAlertInfo({severity: 'warning', message: 'Por favor, preencha todos os campos.'});
      setGeneratedDesignation({ designation: "Preencha todos os campos!" });
      return;
    }

    setIsLoading(true);

    const requestBody = {
      cnl: selectedMunicipality.acronym,
      contractId: contractId.trim(),
      circuitType: circuitType,
    };

    try {
      const response = await fetch(`${BaseURL}/designations-generator`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestBody),
      });

      if (!response.ok) {
        throw new Error(`Erro na API: ${response.statusText}`);
      }

      const finalDesignation = await response.text();

      setGeneratedDesignation({ designation: finalDesignation });
      await navigator.clipboard.writeText(finalDesignation);
      setAlertInfo({ severity: 'success', message: 'Designação gerada e copiada para a área de transferência!' });

    } catch (error) {
      console.error("Falha ao gerar designação:", error);
      setAlertInfo({ severity: 'error', message: 'Falha ao gerar designação. Verifique o console.' });
      setGeneratedDesignation({ designation: "Erro ao gerar designação." });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="container">
      {alertInfo && (
        <ActionAlert
          severity={alertInfo.severity}
          message={alertInfo.message}
          onClose={handleCloseAlert}
        />
      )}

        <header>
            <h2>Gerador de Designação</h2>
        </header>
      <form className="designation-form">
        
        <div className= "section-container">
          <p>Selecione o município:</p>
          <SearchBoxAutoComponent
            label= "Cidade:"
            placeholder="Digite o nome da cidade"
            value={selectedMunicipality}
            onChange={setSelectedMunicipality}
          />
          <OutputCNLBox 
            municipality={selectedMunicipality}
            label="CNL:"
            placeholder="CNL selecionado"
           />
        </div>
        <div className="line"/>
        <div className="section-container">
          <p>ID contrato:</p>
          <InputBox 
            label="ID do contrato:"
            placeholder="Digite o ID do Contrato"
            onChange={(e) => setContractId(e.target.value)}
          />
        </div>
        <div className="line"/>
        <div className="section-container">
          <p>Tipo de circuito:</p>
          <SelectTextFields
            value={circuitType}
            onChange={(e) => setCircuitType(e.target.value)}
          />
        </div>
        <div className="line"/>
        <div className="section-container">
          <p>Designação:</p>
          <OutputDesignationBox
            designation={generatedDesignation}
            label="Designação:"
            placeholder="Designação gerada"
          />
          <GenerateAndCopyDesignationButton
            onClick={handleGenerateClick}
            isLoading={isLoading}
          />
        </div>
      </form>
    </div>
  );
};

export default DesignationsGenPage;