import OutputBox from "../../components/OutputBoxComponent/OutputBoxComponent";
import SearchBoxAutoComponent from "../../components/SearchBocAutoComponent/SearchBoxAutoComponent";
import './style.css';

function DesignationsGenPage() {
  return (
    <div className="container">
        <header>
            <h2>Gerador de Designação</h2>
        </header>
      <SearchBoxAutoComponent />
      <OutputBox label={""} value={""} onChange={function (e: React.ChangeEvent<HTMLInputElement>): void {
        throw new Error("Function not implemented.");
      } }/>
    </div>
  );
};

export default DesignationsGenPage;