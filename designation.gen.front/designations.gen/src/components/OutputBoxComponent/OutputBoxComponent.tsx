import './ContentTextBoxStyle.css'

interface InputProps {
  label: string;
  type?: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  placeholder?: string;
}

const Input: React.FC<InputProps> = ({ label, type = 'text', value, onChange, placeholder }) => {
  return (
    <div className="input-wrapper">
      <select id="cnlSelect" className="output-field" required disabled>
                    <option value="" selected>Aguardando pesquisa...</option>
                </select>
    </div>
  );
};

export default Input;