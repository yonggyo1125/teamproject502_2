import React from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import InputBox from '../../commons/components/InputBox';

const FormBox = styled.form`

`;

const ProfileForm = ({form, onChange, onSubmit }) => {

    return <FormBox onSubmit={onSubmit} autoComplete='off'>

    </FormBox>
};

export default React.memo(ProfileForm);
