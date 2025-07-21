import { useNavigate } from "react-router-dom";

export default function MenuPage() {
    const navigate = useNavigate();

    return (
        <div>
            <h1>Menu</h1>
            <button type="button"  onClick={() => { navigate('/DesignationsGenPage')}}>Gerar designações</button>
            <button type="button" onClick={() => { navigate('/CNLQueryPage')}}>Visualizar CNL's</button>
        </div>
    );
}