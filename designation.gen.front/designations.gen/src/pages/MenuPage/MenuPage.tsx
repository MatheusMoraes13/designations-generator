import { useNavigate } from "react-router-dom";

import './MenuPageStyle.css';

export default function MenuPage() {
    const navigate = useNavigate();

    return (
        <div className="menu-container">
            <h1>Menu de Designações</h1>
            <button type="button"  onClick={() => { navigate('/DesignationsGenPage')}}>Gerar designações</button>
            <button type="button" onClick={() => { navigate('/CNLQueryPage')}}>Visualizar CNL's</button>
        </div>
    );
}