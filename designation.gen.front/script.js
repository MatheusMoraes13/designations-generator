document.addEventListener('DOMContentLoaded', () => {
    // --- Elementos do DOM ---
    const searchInput = document.getElementById('municipalitySearch');
    const searchBtn = document.getElementById('searchBtn');
    const cnlSelect = document.getElementById('cnlSelect');
    const searchError = document.getElementById('searchError');
    const contractIdInput = document.getElementById('contractId');
    const circuitTypeSelect = document.getElementById('circuitType');
    const generatedDesignationInput = document.getElementById('generatedDesignation');
    const form = document.getElementById('designationForm');
    const generateBtn = document.getElementById('generateBtn');

    const API_BASE_URL = 'http://localhost:8080'; // Altere se sua API rodar em outra porta

    // --- Funções ---

    /**
     * Pesquisa o município na API e preenche o select de CNL.
     */
    const findMunicipality = async () => {
        const municipalityName = searchInput.value.trim();
        if (!municipalityName) {
            searchError.textContent = 'Por favor, digite o nome de um município.';
            return;
        }

        searchError.textContent = '';
        cnlSelect.disabled = true;
        cnlSelect.innerHTML = '<option>Buscando...</option>';

        try {
            const response = await fetch(`${API_BASE_URL}/municipalities/findbyname`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ name: municipalityName })
            });

            // A API retorna 204 (NO_CONTENT) se não encontrar, então tratamos isso primeiro
            if (response.status === 204 || response.status === 404) {
                 searchError.textContent = `Nenhum município encontrado para "${municipalityName}".`;
                 cnlSelect.innerHTML = '<option value="">Falha na busca</option>';
                 return;
            }

            if (!response.ok) {
                throw new Error(`Erro na API: ${response.statusText}`);
            }

            // O corpo da resposta é um objeto Municipalities dentro de uma lista opcional
            const municipalityData = await response.json();
            
            // Tratando o caso de um Optional<Municipalities> que pode vir como um objeto direto
             const municipality = Array.isArray(municipalityData) ? municipalityData[0] : municipalityData;


            if (municipality && municipality.acronym) {
                cnlSelect.innerHTML = `<option value="${municipality.acronym}">${municipality.acronym}</option>`;
                cnlSelect.disabled = false;
            } else {
                 searchError.textContent = 'A resposta da API não continha uma sigla CNL.';
                 cnlSelect.innerHTML = '<option value="">Erro</option>';
            }

        } catch (error) {
            console.error('Falha ao buscar município:', error);
            searchError.textContent = 'Erro de conexão com a API.';
            cnlSelect.innerHTML = '<option value="">Erro</option>';
        }
    };

    /**
     * Gera a designação final, exibe e copia para a área de transferência.
     */
    const generateAndCopyDesignation = async (event) => {
        event.preventDefault(); // Impede o envio padrão do formulário

        // Validação dos campos
        if (!contractIdInput.value || cnlSelect.value === '') {
            generatedDesignationInput.value = 'Preencha todos os campos primeiro!';
            return;
        }

        const designationData = {
            cnl: cnlSelect.value,
            contractId: contractIdInput.value.trim(),
            circuitType: circuitTypeSelect.value
        };

        try {
            const response = await fetch(`${API_BASE_URL}/designations-generator`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(designationData)
            });

            if (!response.ok) {
                throw new Error(`Erro na API: ${response.statusText}`);
            }

            const finalDesignation = await response.text();
            generatedDesignationInput.value = finalDesignation;

            // Copiando para a área de transferência
            await navigator.clipboard.writeText(finalDesignation);

            // Feedback visual para o usuário
            const originalButtonText = generateBtn.textContent;
            generateBtn.textContent = '✅ Copiado!';
            generateBtn.style.backgroundColor = 'var(--success-color)';
            
            setTimeout(() => {
                generateBtn.textContent = originalButtonText;
                generateBtn.style.backgroundColor = 'var(--primary-color)';
            }, 2000);

        } catch (error) {
            console.error('Falha ao gerar designação:', error);
            generatedDesignationInput.value = 'Erro ao gerar. Verifique a API.';
        }
    };

    // --- Event Listeners ---
    searchBtn.addEventListener('click', findMunicipality);
    // Permite buscar pressionando Enter no campo de pesquisa
    searchInput.addEventListener('keydown', (event) => {
        if (event.key === 'Enter') {
            event.preventDefault();
            findMunicipality();
        }
    });

    form.addEventListener('submit', generateAndCopyDesignation);
});