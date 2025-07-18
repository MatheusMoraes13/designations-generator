import './InputStyle.css'

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
      <label className="input-label">{label}</label>
      <input
        className="input-field"
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
      />
    </div>
  );
};

export default Input;