import TextField from '@mui/material/TextField';
import type { DesignationResponse } from '../../data/DesignationsData';

interface OutputBoxProps {
  designation: DesignationResponse | null;
  label: string;
  placeholder: string;
}

export default function OutputDesignationBox(OutpuBoxProps: OutputBoxProps) {
  return (
    
      <div>
        <TextField
          id="ReadOnlyOutput"
          label={OutpuBoxProps.label}
          placeholder={OutpuBoxProps.placeholder}
          value={OutpuBoxProps?.designation?.designation?? ''}
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