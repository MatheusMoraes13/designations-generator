import TextField from '@mui/material/TextField';

interface InputBoxProps {
  idContract: string;
}

export default function InputBox () {
  return (
    <div>
      <TextField
          required
          id="outlined-required"
          label="ID Contrato"
          defaultValue="ID do contrato do circuito"
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
};