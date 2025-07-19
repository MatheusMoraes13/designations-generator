import './GenerateAndCopyButtonStyle.css';

interface GenerateButtonProps {
  onClick: () => void;
  isLoading: boolean;
}

export default function GenerateAndCopyDesignationButton({ onClick, isLoading }: GenerateButtonProps) {
  return (
    <button type="button" onClick={onClick} disabled={isLoading}>
      {isLoading ? 'Gerando...' : 'Gerar e Copiar'}
    </button>
  );
}