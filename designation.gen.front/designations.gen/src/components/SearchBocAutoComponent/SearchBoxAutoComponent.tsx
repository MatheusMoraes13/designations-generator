import { useState, useEffect } from 'react';
import TextField from '@mui/material/TextField';
import Autocomplete from '@mui/material/Autocomplete';
import type { Municipalities } from '../../data/MunicipalitiesData';
import { GetAllMunicipalities } from '../../functions/MunicipalitiesAPIFunctions';

export default function SearchBoxAutoComponent() {
  const [options, setOptions] = useState<Municipalities[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await GetAllMunicipalities();
        setOptions(data); 
      } catch (err) {
        console.error("Falha ao buscar municípios:", err);
        setError(err as Error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  return (
    <Autocomplete
      disablePortal
      options={options}
      loading={loading}
      getOptionLabel={(option) => option.name}
      sx={{ width: 300 }}
      renderInput={(params) => <TextField {...params} label="Cidade" sx={{
            '& .MuiOutlinedInput-input': {
              color: '#ffffff',
            },
            '& .MuiSvgIcon-root': {
              color: '#ced4da',
            },
            '& .MuiOutlinedInput-root': {
              backgroundColor: '#212529', // Fundo escuro do input
              '& fieldset': {
                borderColor: '#495057', // Borda cinza sutil
              },
              '&:hover fieldset': {
                borderColor: '#6ea8fe',
              },
              '&.Mui-focused fieldset': {
                borderColor: '#6ea8fe',
              },
            },
            '& .MuiInputLabel-root': {
              color: '#ced4da', // Cor do label
            },
            '& .MuiInputLabel-root.Mui-focused': {
                color: '#6ea8fe',
            },
          }}
        
        />}
    />
  );
}
