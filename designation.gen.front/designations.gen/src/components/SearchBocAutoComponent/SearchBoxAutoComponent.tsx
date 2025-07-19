import { useState, useEffect } from 'react';
import TextField from '@mui/material/TextField';
import Autocomplete from '@mui/material/Autocomplete';
import type { Municipalities } from '../../data/MunicipalitiesData';
import { GetAllMunicipalities } from '../../functions/MunicipalitiesAPIFunctions';
import { SetCachedSelectedMUnicipalities } from '../../functions/MunicipalitiesAPIFunctions';
import { GetCachedSelectedMunicipalities } from '../../functions/MunicipalitiesAPIFunctions';


interface SearchBoxProps {
  value: Municipalities | null;
  onChange: (newValue: Municipalities | null) => void;
  label: string;
  placeholder: string;
}


export default function SearchBoxAutoComponent({ value, onChange, label, placeholder }: SearchBoxProps) {
  const [options, setOptions] = useState<Municipalities[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      console.log("Buscando municípios...");
      setLoading(true);
      try {
        const data = await GetAllMunicipalities();
        console.log("Dados recebidos:", data);
        setOptions(Array.isArray(data) ? data : []); 
      } catch (err) {
        console.error("Falha ao buscar municípios:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  return (
    <>
    <Autocomplete
      value={value}
      onChange={(event: any, newValue: Municipalities | null) => {
        onChange(newValue);
        SetCachedSelectedMUnicipalities(newValue);
        console.log(GetCachedSelectedMunicipalities());
      }}
      getOptionKey={(option) => option.id}  
      options={options}
      loading={loading}
      getOptionLabel={(option) => option.name + " - " + option.acronym }
      sx={{ width: 300 }}
      slotProps={{
        paper: {
          sx: {
            bgcolor: 'var(--container-bg)',
            color: 'var(--text-color)',
            mt: 1,
            boxShadow: '0px 4px 10px rgba(0,0,0,0.5)',
            border: 'var(--border-color) solid 1px',
            borderRadius: '6px',
          }
        },
        listbox: {
          sx: {
            maxHeight: 200,
            '& .MuiAutocomplete-option': {
            padding: '8px 16px',
            '&:not(:last-child)': {
              borderBottom: '1px solid #2b3035ff',
            },
            '&[aria-selected="true"]': {
              backgroundColor: 'var(--backgound-color)',
            },
            '&:hover': {
                backgroundColor: 'var(--primary-color)',
                color: 'var(--text-color)',
              },
            }
          }
        },
        popper: {
          sx: {
            zIndex: 1400,
          }
        }
      }}
      renderInput={(params) => <TextField {...params} 
        label= {label} 
        placeholder= {placeholder}
        InputLabelProps={{
          shrink: true,
        }}
        sx={{
          marginTop: '10px',
          '& .MuiOutlinedInput-input': {
            color: 'var(--text-color)',
          },
          '& .MuiSvgIcon-root': {
            color: 'var(--border-color)',
          },
          '& .MuiOutlinedInput-root': {
            height: '45px',
            backgroundColor: 'var(--container-bg)',
            '& fieldset': {
              borderColor: 'var(--border-color)',
            },
            '&:hover fieldset': {
              borderColor: 'var(--primary-color)',
            },
            '&.Mui-focused fieldset': {
              borderColor: 'var(--primary-color)',
            },
          },
          '& .MuiInputLabel-root': {
            color: 'var(--text-color)',
          },
          '& .MuiInputLabel-root.Mui-focused': {
          color: 'var(--primary-color)',
          },
        }}
        />}
    />
    </>
  );
}