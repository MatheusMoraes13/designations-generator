import TextField from '@mui/material/TextField';
import type { Municipalities } from '../../data/MunicipalitiesData';

interface OutputBoxProps {
  municipality: Municipalities | null;
  label: string;
  placeholder: string;
}

export default function OutputCNLBox(OutpuBoxProps: OutputBoxProps) {
  return (
    
      <div>
        <TextField
          id="ReadOnlyOutput"
          label={OutpuBoxProps.label}
          placeholder={OutpuBoxProps.placeholder}
          value={OutpuBoxProps?.municipality?.acronym ?? ''}
          slotProps={{
            input: {
              readOnly: true,
            },
            inputLabel: {
              shrink: true,
            }
          }}
          sx={{
            width: 300,
            bgcolor: 'var(--container-bg)',
            color: 'var(--text-color)',
            mt: 1,
            borderRadius: '6px',
            '& .MuiOutlinedInput-input': {
              color: 'var(--text-color)',
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
            '& .MuiInputLabel-root.Mui-focused': {
                color: 'var(--primary-color)',
            },
            '& .MuiInputLabel-root': {
              color: 'var(--text-color)',
            },
          }}
        />
      </div>
  );
}