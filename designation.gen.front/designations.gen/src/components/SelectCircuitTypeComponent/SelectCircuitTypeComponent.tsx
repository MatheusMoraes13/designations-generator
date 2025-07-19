import TextField from '@mui/material/TextField';
import MenuItem from '@mui/material/MenuItem';

const cyrcuitsTypes = [
  {
    value: 'L2',
    label: 'Transporte L2',
  },
  {
    value: 'IP',
    label: 'Link IP',
  },
];

interface SelectProps {
  value: string;
  onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

export default function SelectTextFields(selectProps: SelectProps) {
  return (
    <TextField
      id="outlined-select-circuit-type"
      select
      label="Tipo de Circuito"
      defaultValue="IP"
      value={selectProps.value}
      onChange={selectProps.onChange}
      sx={{
        width: 300,
        '& .MuiOutlinedInput-root': {
          height: '45px',
          backgroundColor: 'var(--container-bg)',
          borderRadius: '6px',
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
        '& .MuiSelect-select': {
          color: 'var(--text-color)',
        },
        '& .MuiSvgIcon-root': {
          color: 'var(--border-color)',
        },
      }}
      SelectProps={{
        MenuProps: {
          slotProps: {
            paper: {
              sx: {
                bgcolor: 'var(--container-bg)',
                color: 'var(--text-color)',
                marginTop: '8px',
                border: '1px solid var(--border-color)',
                boxShadow: '0px 4px 10px rgba(0,0,0,0.5)',
              },
            },
          },
        },
      }}
    >
      {cyrcuitsTypes.map((option) => (
        <MenuItem
          key={option.value}
          value={option.value}
          sx={{
            '&:hover': {
              backgroundColor: 'var(--primary-color)',
            },
            '&.Mui-selected': {
              backgroundColor: '#222f3e',
            },
            '&.Mui-selected:hover': {
              backgroundColor: 'var(--primary-color)',
            },
          }}
        >
          {option.label}
        </MenuItem>
      ))}
    </TextField>
  );
}