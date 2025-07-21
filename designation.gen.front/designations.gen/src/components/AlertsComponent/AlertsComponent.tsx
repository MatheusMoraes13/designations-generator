import Slide from '@mui/material/Slide';
import Alert from '@mui/material/Alert';
import Stack from '@mui/material/Stack';
import { useEffect } from 'react';


export interface ActionAlertProps {
  severity: 'success' | 'error' | 'warning' | 'info';
  message: string;
  onClose?: () => void;
}

export default function ActionAlert({ severity, message, onClose }: ActionAlertProps) {

    useEffect(() => {
        if (!message) {
          return;
        }

        const timer = setTimeout(() => {
            onClose?.();
        }, 5000);

        return () => {
          clearTimeout(timer);
        };
      }, [message, onClose]);

    if (!message) {
        return null;
    }

    return (
        <Slide direction="down" in={!!message} mountOnEnter unmountOnExit>
            <Stack sx={{    width: { xs: '90%', sm: '60%', md: '80%' }, 
                            position: 'fixed', 
                            top: '0', 
                            left: '0',  
                            zIndex: 1500 
                        }}>
              <Alert 
                variant="filled" 
                severity={severity} onClose={onClose}
                sx={{
                    backgroundColor:
                    severity === 'success' ? 'var(--success-color)' :
                    severity === 'error' ? 'var(--error-color)' :
                    severity === 'warning' ? '#f57c00' :
                    '#1976d2',
                }}
                >
                {message}
              </Alert>
            </Stack>
        </Slide>
    );
}