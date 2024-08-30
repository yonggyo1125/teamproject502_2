import React from 'react';
import styled from 'styled-components';
import FileUpload from '../../commons/components/FileUpload';
import NoProfile from '../../images/profile.webp';

const Wrapper = styled.div``;

const ProfileImage = ({ gid, profileImage, fileUploadCallback }) => {
  return (
    <Wrapper>
      <FileUpload
        imageUrl={profileImage ?? NoProfile}
        gid={gid}
        imageOnly={true}
        single={true}
        done={true}
        callback={fileUploadCallback}
      />
    </Wrapper>
  );
};

export default React.memo(ProfileImage);
