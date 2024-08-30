import React, { useState, useCallback, useContext } from 'react';
import { useTranslation } from 'react-i18next';
import UserInfoContext from '../../member/modules/UserInfoContext';
import ProfileForm from '../component/ProfileForm';

const MypageProfileContainer = () => {
  const {
    states: { userInfo },
  } = useContext(UserInfoContext);
  const initialForm = userInfo;
  delete initialForm.password;

  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});

  const { t } = useTranslation();

  const onChange = useCallback((e) => {
    setForm((form) => ({ ...form, [e.target.name]: e.target.value }));
  }, []);

  const onSubmit = useCallback(
    (e) => {
      e.preventDefault();

      const _errors = {};
      let hasErrors = false;
      
      /**
       * 필수항목 검증
       * 1. 회원명(이름)
       * 2. 비밀번호(선택), 있는 경우 confirmPassword(필수), password, confirmPassword 일치여부
       */
      const requiredFields = {
        userName: t('회원명을_입력하세요'),
      };
      if (form?.password?.trim()) {
        requiredFields.confirmPassword = t('비밀번호를_확인하세요.');
      }
    },
    [t, form],
  );

  return (
    <ProfileForm
      form={form}
      onChange={onChange}
      onSubmit={onSubmit}
      errors={errors}
    />
  );
};

export default React.memo(MypageProfileContainer);
